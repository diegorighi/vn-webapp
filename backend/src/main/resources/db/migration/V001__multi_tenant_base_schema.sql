-- ============================================================================
-- V001: Multi-Tenant Base Schema
-- ============================================================================
-- This migration creates the foundation for multi-tenant architecture with:
-- 1. Account (tenant) isolation
-- 2. User management with RBAC
-- 3. Approval workflow for data changes
-- 4. Row-Level Security policies
-- ============================================================================

-- ============================================================================
-- ENUM TYPES
-- ============================================================================

CREATE TYPE account_status AS ENUM ('ACTIVE', 'SUSPENDED', 'BLOCKED', 'TRIAL');
CREATE TYPE user_status AS ENUM ('ACTIVE', 'INACTIVE', 'BLOCKED', 'PENDING_ACTIVATION');
CREATE TYPE role_type AS ENUM ('OWNER', 'ADMIN', 'MANAGER', 'EDITOR', 'VIEWER');
CREATE TYPE approval_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED', 'AUTO_APPROVED');
CREATE TYPE entity_type AS ENUM (
    'CLIENTE', 'CONTA_PROGRAMA', 'TRANSACAO', 'VIAGEM',
    'PROGRAMA_MILHAS', 'CREDENCIAL', 'SALDO_MILHAS'
);
CREATE TYPE operation_type AS ENUM ('INSERT', 'UPDATE', 'DELETE');

-- ============================================================================
-- ACCOUNT (TENANT) TABLE
-- ============================================================================

CREATE TABLE account (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Identification
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,  -- URL-friendly identifier
    cnpj VARCHAR(18) UNIQUE,            -- Brazilian company ID (optional)

    -- Contact
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20),

    -- Status & Settings
    status account_status NOT NULL DEFAULT 'TRIAL',
    max_users INT NOT NULL DEFAULT 5,
    settings JSONB NOT NULL DEFAULT '{}',

    -- Subscription
    plan VARCHAR(50) NOT NULL DEFAULT 'FREE',
    trial_ends_at TIMESTAMP,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Constraints
    CONSTRAINT account_name_not_empty CHECK (LENGTH(TRIM(name)) > 0),
    CONSTRAINT account_slug_valid CHECK (slug ~ '^[a-z0-9-]+$'),
    CONSTRAINT account_email_valid CHECK (email ~ '^[^@]+@[^@]+\.[^@]+$')
);

CREATE INDEX idx_account_slug ON account(slug);
CREATE INDEX idx_account_status ON account(status);
CREATE INDEX idx_account_email ON account(email);

-- ============================================================================
-- USER TABLE
-- ============================================================================

CREATE TABLE "user" (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID NOT NULL REFERENCES account(id) ON DELETE CASCADE,

    -- Authentication
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,

    -- Profile
    name VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(500),
    phone VARCHAR(20),

    -- Status
    status user_status NOT NULL DEFAULT 'PENDING_ACTIVATION',
    email_verified_at TIMESTAMP,
    last_login_at TIMESTAMP,
    failed_login_attempts INT NOT NULL DEFAULT 0,
    locked_until TIMESTAMP,

    -- Settings
    preferences JSONB NOT NULL DEFAULT '{}',
    timezone VARCHAR(50) NOT NULL DEFAULT 'America/Sao_Paulo',
    locale VARCHAR(10) NOT NULL DEFAULT 'pt-BR',

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Constraints
    CONSTRAINT user_email_unique_per_account UNIQUE (account_id, email),
    CONSTRAINT user_name_not_empty CHECK (LENGTH(TRIM(name)) > 0)
);

CREATE INDEX idx_user_account ON "user"(account_id);
CREATE INDEX idx_user_email ON "user"(email);
CREATE INDEX idx_user_status ON "user"(status);

-- ============================================================================
-- ROLE TABLE (Predefined roles per account)
-- ============================================================================

CREATE TABLE role (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID NOT NULL REFERENCES account(id) ON DELETE CASCADE,

    -- Role definition
    type role_type NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,

    -- Capabilities (stored as JSONB for flexibility)
    permissions JSONB NOT NULL DEFAULT '[]',

    -- Flags
    is_system BOOLEAN NOT NULL DEFAULT FALSE,  -- System roles can't be deleted
    can_approve BOOLEAN NOT NULL DEFAULT FALSE, -- Can approve pending changes

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Each account has unique role names
    CONSTRAINT role_unique_per_account UNIQUE (account_id, type)
);

CREATE INDEX idx_role_account ON role(account_id);
CREATE INDEX idx_role_type ON role(type);

-- ============================================================================
-- USER_ROLE (Many-to-Many)
-- ============================================================================

CREATE TABLE user_role (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES role(id) ON DELETE CASCADE,

    -- Assignment metadata
    assigned_by UUID REFERENCES "user"(id),
    assigned_at TIMESTAMP NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMP,  -- Optional expiration

    -- Constraints
    CONSTRAINT user_role_unique UNIQUE (user_id, role_id)
);

CREATE INDEX idx_user_role_user ON user_role(user_id);
CREATE INDEX idx_user_role_role ON user_role(role_id);

-- ============================================================================
-- APPROVAL REQUEST TABLE (Workflow for pending changes)
-- ============================================================================

CREATE TABLE approval_request (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID NOT NULL REFERENCES account(id) ON DELETE CASCADE,

    -- Request details
    entity_type entity_type NOT NULL,
    entity_id UUID,  -- NULL for INSERT (entity doesn't exist yet)
    operation operation_type NOT NULL,

    -- Data payload
    data_before JSONB,      -- Previous state (for UPDATE/DELETE)
    data_after JSONB NOT NULL,  -- New/proposed state

    -- Request metadata
    requested_by UUID NOT NULL REFERENCES "user"(id),
    requested_at TIMESTAMP NOT NULL DEFAULT NOW(),
    reason TEXT,  -- Optional justification

    -- Approval metadata
    status approval_status NOT NULL DEFAULT 'PENDING',
    reviewed_by UUID REFERENCES "user"(id),
    reviewed_at TIMESTAMP,
    review_comment TEXT,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Constraints
    CONSTRAINT approval_valid_operation CHECK (
        (operation = 'INSERT' AND entity_id IS NULL AND data_before IS NULL) OR
        (operation = 'UPDATE' AND entity_id IS NOT NULL) OR
        (operation = 'DELETE' AND entity_id IS NOT NULL AND data_after IS NOT NULL)
    )
);

CREATE INDEX idx_approval_account ON approval_request(account_id);
CREATE INDEX idx_approval_status ON approval_request(status);
CREATE INDEX idx_approval_entity ON approval_request(entity_type, entity_id);
CREATE INDEX idx_approval_requested_by ON approval_request(requested_by);
CREATE INDEX idx_approval_pending ON approval_request(account_id, status) WHERE status = 'PENDING';

-- ============================================================================
-- AUDIT LOG TABLE
-- ============================================================================

CREATE TABLE audit_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID NOT NULL REFERENCES account(id) ON DELETE CASCADE,

    -- Actor
    user_id UUID REFERENCES "user"(id),
    user_email VARCHAR(255),  -- Denormalized for history

    -- Action
    action VARCHAR(100) NOT NULL,
    entity_type entity_type,
    entity_id UUID,

    -- Request context
    ip_address INET,
    user_agent TEXT,
    request_id UUID,  -- For correlation

    -- Data
    old_data JSONB,
    new_data JSONB,
    metadata JSONB NOT NULL DEFAULT '{}',

    -- Timestamp (immutable)
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Partition by month for performance (optional - implement if needed)
CREATE INDEX idx_audit_account ON audit_log(account_id);
CREATE INDEX idx_audit_user ON audit_log(user_id);
CREATE INDEX idx_audit_entity ON audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_created ON audit_log(created_at DESC);
CREATE INDEX idx_audit_action ON audit_log(action);

-- ============================================================================
-- SESSION TABLE (for refresh tokens / sessions)
-- ============================================================================

CREATE TABLE user_session (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,

    -- Token
    refresh_token_hash VARCHAR(255) NOT NULL UNIQUE,

    -- Context
    ip_address INET,
    user_agent TEXT,
    device_info JSONB,

    -- Validity
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP,

    -- Last activity
    last_used_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_session_user ON user_session(user_id);
CREATE INDEX idx_session_token ON user_session(refresh_token_hash);
CREATE INDEX idx_session_expires ON user_session(expires_at) WHERE revoked_at IS NULL;

-- ============================================================================
-- INVITATION TABLE (for inviting users to account)
-- ============================================================================

CREATE TABLE invitation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID NOT NULL REFERENCES account(id) ON DELETE CASCADE,

    -- Invitation details
    email VARCHAR(255) NOT NULL,
    role_id UUID NOT NULL REFERENCES role(id),

    -- Token
    token_hash VARCHAR(255) NOT NULL UNIQUE,

    -- Metadata
    invited_by UUID NOT NULL REFERENCES "user"(id),
    message TEXT,

    -- Status
    accepted_at TIMESTAMP,
    accepted_by UUID REFERENCES "user"(id),
    expires_at TIMESTAMP NOT NULL,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Constraints
    CONSTRAINT invitation_not_expired CHECK (expires_at > created_at)
);

CREATE INDEX idx_invitation_account ON invitation(account_id);
CREATE INDEX idx_invitation_email ON invitation(email);
CREATE INDEX idx_invitation_token ON invitation(token_hash);

-- ============================================================================
-- FUNCTION: Initialize default roles for new account
-- ============================================================================

CREATE OR REPLACE FUNCTION create_default_roles_for_account()
RETURNS TRIGGER AS $$
BEGIN
    -- OWNER role (highest privilege)
    INSERT INTO role (account_id, type, name, description, permissions, is_system, can_approve)
    VALUES (
        NEW.id,
        'OWNER',
        'Proprietário',
        'Acesso total à conta. Pode gerenciar usuários, aprovar alterações e configurar a conta.',
        '["*"]'::jsonb,
        TRUE,
        TRUE
    );

    -- ADMIN role
    INSERT INTO role (account_id, type, name, description, permissions, is_system, can_approve)
    VALUES (
        NEW.id,
        'ADMIN',
        'Administrador',
        'Pode gerenciar usuários e aprovar alterações pendentes.',
        '["users:read", "users:write", "data:read", "data:write", "data:delete", "approvals:manage"]'::jsonb,
        TRUE,
        TRUE
    );

    -- MANAGER role
    INSERT INTO role (account_id, type, name, description, permissions, is_system, can_approve)
    VALUES (
        NEW.id,
        'MANAGER',
        'Gerente',
        'Pode visualizar e editar dados, mas precisa de aprovação para alterações.',
        '["data:read", "data:write", "reports:read"]'::jsonb,
        TRUE,
        FALSE
    );

    -- EDITOR role
    INSERT INTO role (account_id, type, name, description, permissions, is_system, can_approve)
    VALUES (
        NEW.id,
        'EDITOR',
        'Editor',
        'Pode visualizar e propor alterações. Todas as alterações precisam de aprovação.',
        '["data:read", "data:write:pending"]'::jsonb,
        TRUE,
        FALSE
    );

    -- VIEWER role (lowest privilege)
    INSERT INTO role (account_id, type, name, description, permissions, is_system, can_approve)
    VALUES (
        NEW.id,
        'VIEWER',
        'Visualizador',
        'Apenas visualização. Não pode fazer alterações.',
        '["data:read"]'::jsonb,
        TRUE,
        FALSE
    );

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_create_default_roles
    AFTER INSERT ON account
    FOR EACH ROW
    EXECUTE FUNCTION create_default_roles_for_account();

-- ============================================================================
-- FUNCTION: Update timestamp trigger
-- ============================================================================

CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply to all tables with updated_at
CREATE TRIGGER trigger_account_updated_at
    BEFORE UPDATE ON account FOR EACH ROW EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trigger_user_updated_at
    BEFORE UPDATE ON "user" FOR EACH ROW EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trigger_role_updated_at
    BEFORE UPDATE ON role FOR EACH ROW EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trigger_approval_updated_at
    BEFORE UPDATE ON approval_request FOR EACH ROW EXECUTE FUNCTION update_timestamp();

-- ============================================================================
-- ROW-LEVEL SECURITY SETUP
-- ============================================================================

-- Enable RLS on all multi-tenant tables
ALTER TABLE "user" ENABLE ROW LEVEL SECURITY;
ALTER TABLE role ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_role ENABLE ROW LEVEL SECURITY;
ALTER TABLE approval_request ENABLE ROW LEVEL SECURITY;
ALTER TABLE audit_log ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_session ENABLE ROW LEVEL SECURITY;
ALTER TABLE invitation ENABLE ROW LEVEL SECURITY;

-- ============================================================================
-- RLS POLICIES
-- ============================================================================
-- Note: Policies use current_setting('app.current_account_id') which must be
-- set by the application at the start of each request/transaction.

-- User policies
CREATE POLICY user_isolation ON "user"
    FOR ALL
    USING (account_id = current_setting('app.current_account_id', true)::uuid)
    WITH CHECK (account_id = current_setting('app.current_account_id', true)::uuid);

-- Role policies
CREATE POLICY role_isolation ON role
    FOR ALL
    USING (account_id = current_setting('app.current_account_id', true)::uuid)
    WITH CHECK (account_id = current_setting('app.current_account_id', true)::uuid);

-- User_role policies (through user's account)
CREATE POLICY user_role_isolation ON user_role
    FOR ALL
    USING (
        EXISTS (
            SELECT 1 FROM "user" u
            WHERE u.id = user_role.user_id
            AND u.account_id = current_setting('app.current_account_id', true)::uuid
        )
    );

-- Approval request policies
CREATE POLICY approval_isolation ON approval_request
    FOR ALL
    USING (account_id = current_setting('app.current_account_id', true)::uuid)
    WITH CHECK (account_id = current_setting('app.current_account_id', true)::uuid);

-- Audit log policies (read-only for non-admins, admins see all for their account)
CREATE POLICY audit_isolation ON audit_log
    FOR ALL
    USING (account_id = current_setting('app.current_account_id', true)::uuid)
    WITH CHECK (account_id = current_setting('app.current_account_id', true)::uuid);

-- Session policies
CREATE POLICY session_isolation ON user_session
    FOR ALL
    USING (
        EXISTS (
            SELECT 1 FROM "user" u
            WHERE u.id = user_session.user_id
            AND u.account_id = current_setting('app.current_account_id', true)::uuid
        )
    );

-- Invitation policies
CREATE POLICY invitation_isolation ON invitation
    FOR ALL
    USING (account_id = current_setting('app.current_account_id', true)::uuid)
    WITH CHECK (account_id = current_setting('app.current_account_id', true)::uuid);

-- ============================================================================
-- APPLICATION ROLES (PostgreSQL roles)
-- ============================================================================

-- Role for the application (cannot bypass RLS)
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'app_user') THEN
        CREATE ROLE app_user NOINHERIT;
    END IF;
END
$$;

-- Grant permissions to app_user
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_user;

-- Ensure RLS is enforced
ALTER ROLE app_user SET row_security = on;

-- Admin role that can bypass RLS (for system operations only)
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'app_admin') THEN
        CREATE ROLE app_admin BYPASSRLS;
    END IF;
END
$$;

GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_admin;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_admin;

-- ============================================================================
-- COMMENTS
-- ============================================================================

COMMENT ON TABLE account IS 'Tenant/organization that owns all data. Root of multi-tenant isolation.';
COMMENT ON TABLE "user" IS 'Users belonging to an account. Email is unique per account.';
COMMENT ON TABLE role IS 'Predefined roles with permissions. System roles created automatically.';
COMMENT ON TABLE user_role IS 'Association between users and their roles.';
COMMENT ON TABLE approval_request IS 'Pending data changes awaiting admin approval (PR-like workflow).';
COMMENT ON TABLE audit_log IS 'Immutable log of all actions for compliance and debugging.';
COMMENT ON TABLE user_session IS 'Active user sessions with refresh tokens.';
COMMENT ON TABLE invitation IS 'Pending invitations to join an account.';

COMMENT ON COLUMN approval_request.data_before IS 'State before change (NULL for INSERT)';
COMMENT ON COLUMN approval_request.data_after IS 'Proposed new state';
COMMENT ON COLUMN approval_request.status IS 'PENDING requires review, AUTO_APPROVED for admins';
