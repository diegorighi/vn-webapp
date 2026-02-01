-- ============================================================================
-- V003: Tenant Encryption Keys (DEK Management)
-- ============================================================================
-- Each account has its own Data Encryption Key (DEK) for encrypting
-- sensitive data like credentials. The DEK itself is encrypted with
-- a Master Key (KEK) stored in KMS/Vault.
-- ============================================================================

-- ============================================================================
-- TENANT KEY TABLE
-- ============================================================================

CREATE TABLE tenant_key (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID NOT NULL REFERENCES account(id) ON DELETE CASCADE,

    -- Key data (DEK encrypted with KEK)
    encrypted_dek BYTEA NOT NULL,
    key_version INT NOT NULL DEFAULT 1,

    -- Key metadata
    algorithm VARCHAR(50) NOT NULL DEFAULT 'AES-256-GCM',
    kek_id VARCHAR(255) NOT NULL,  -- Reference to KMS/Vault key

    -- Key lifecycle
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',  -- ACTIVE, ROTATING, DEPRECATED
    activated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    rotated_at TIMESTAMP,
    deprecated_at TIMESTAMP,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Constraints
    CONSTRAINT tenant_key_unique_version UNIQUE (account_id, key_version),
    CONSTRAINT tenant_key_one_active UNIQUE (account_id, status)
        DEFERRABLE INITIALLY DEFERRED  -- Allow temporary dual-active during rotation
);

CREATE INDEX idx_tenant_key_account ON tenant_key(account_id);
CREATE INDEX idx_tenant_key_status ON tenant_key(status);

-- ============================================================================
-- KEY ROTATION LOG
-- ============================================================================

CREATE TABLE key_rotation_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID NOT NULL REFERENCES account(id) ON DELETE CASCADE,

    -- Rotation details
    old_version INT NOT NULL,
    new_version INT NOT NULL,
    initiated_by UUID REFERENCES "user"(id),

    -- Progress tracking
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',  -- IN_PROGRESS, COMPLETED, FAILED
    total_records INT,
    processed_records INT DEFAULT 0,
    failed_records INT DEFAULT 0,

    -- Timestamps
    started_at TIMESTAMP NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMP,

    -- Error handling
    last_error TEXT
);

CREATE INDEX idx_key_rotation_account ON key_rotation_log(account_id);
CREATE INDEX idx_key_rotation_status ON key_rotation_log(status);

-- ============================================================================
-- ENABLE RLS
-- ============================================================================

ALTER TABLE tenant_key ENABLE ROW LEVEL SECURITY;
ALTER TABLE key_rotation_log ENABLE ROW LEVEL SECURITY;

-- ============================================================================
-- RLS POLICIES
-- ============================================================================

-- Tenant keys (very restricted - only system can access)
CREATE POLICY tenant_key_isolation ON tenant_key
    FOR ALL
    USING (account_id = current_setting('app.current_account_id', true)::uuid)
    WITH CHECK (account_id = current_setting('app.current_account_id', true)::uuid);

-- Key rotation logs
CREATE POLICY key_rotation_isolation ON key_rotation_log
    FOR ALL
    USING (account_id = current_setting('app.current_account_id', true)::uuid)
    WITH CHECK (account_id = current_setting('app.current_account_id', true)::uuid);

-- ============================================================================
-- FUNCTION: Create DEK for new account
-- ============================================================================

-- Note: In production, DEK generation should happen in the application layer
-- using a secure random generator and then encrypted with KMS before storage.
-- This trigger is a placeholder that creates a record to be populated by the app.

CREATE OR REPLACE FUNCTION create_tenant_key_placeholder()
RETURNS TRIGGER AS $$
BEGIN
    -- Insert placeholder - actual encrypted_dek must be set by application
    INSERT INTO tenant_key (
        account_id,
        encrypted_dek,
        key_version,
        algorithm,
        kek_id,
        status
    ) VALUES (
        NEW.id,
        '\x00'::bytea,  -- Placeholder - MUST be replaced by app
        1,
        'AES-256-GCM',
        'pending',  -- App must set actual KEK reference
        'PENDING'
    );

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_create_tenant_key
    AFTER INSERT ON account
    FOR EACH ROW
    EXECUTE FUNCTION create_tenant_key_placeholder();

-- ============================================================================
-- CREDENTIAL ACCESS LOG (for auditing sensitive data access)
-- ============================================================================

CREATE TABLE credential_access_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID NOT NULL REFERENCES account(id) ON DELETE CASCADE,

    -- What was accessed
    credencial_id UUID NOT NULL REFERENCES credencial_programa(id),

    -- Who accessed
    user_id UUID NOT NULL REFERENCES "user"(id),
    user_email VARCHAR(255) NOT NULL,

    -- Access context
    action VARCHAR(50) NOT NULL,  -- READ, DECRYPT, UPDATE
    ip_address INET,
    user_agent TEXT,

    -- Result
    success BOOLEAN NOT NULL,
    error_message TEXT,

    -- Timestamp
    accessed_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_cred_access_account ON credential_access_log(account_id);
CREATE INDEX idx_cred_access_credencial ON credential_access_log(credencial_id);
CREATE INDEX idx_cred_access_user ON credential_access_log(user_id);
CREATE INDEX idx_cred_access_time ON credential_access_log(accessed_at DESC);

ALTER TABLE credential_access_log ENABLE ROW LEVEL SECURITY;

CREATE POLICY cred_access_isolation ON credential_access_log
    FOR ALL
    USING (account_id = current_setting('app.current_account_id', true)::uuid)
    WITH CHECK (account_id = current_setting('app.current_account_id', true)::uuid);

-- ============================================================================
-- GRANTS
-- ============================================================================

GRANT SELECT, INSERT, UPDATE ON tenant_key TO app_user;
GRANT SELECT, INSERT, UPDATE ON key_rotation_log TO app_user;
GRANT SELECT, INSERT ON credential_access_log TO app_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_user;

-- ============================================================================
-- COMMENTS
-- ============================================================================

COMMENT ON TABLE tenant_key IS 'Per-account encryption keys (DEKs). DEK encrypted with master KEK.';
COMMENT ON TABLE key_rotation_log IS 'Log of key rotation operations for audit and recovery.';
COMMENT ON TABLE credential_access_log IS 'Audit log for all credential access (compliance requirement).';

COMMENT ON COLUMN tenant_key.encrypted_dek IS 'DEK encrypted with KEK. Never store plaintext.';
COMMENT ON COLUMN tenant_key.kek_id IS 'Reference to KMS/Vault key used to encrypt DEK.';
