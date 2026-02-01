-- ============================================================================
-- V001: Complete Multi-Tenant Schema for H2 (Development)
-- ============================================================================

-- ============================================================================
-- ACCOUNT (Tenant/Organization)
-- ============================================================================

CREATE TABLE IF NOT EXISTS account (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    cnpj VARCHAR(18),
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'TRIAL',
    max_users INT NOT NULL DEFAULT 5,
    settings CLOB DEFAULT '{}',
    plan VARCHAR(50) NOT NULL DEFAULT 'FREE',
    trial_ends_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_account_slug ON account(slug);
CREATE INDEX idx_account_status ON account(status);

-- ============================================================================
-- ROLE (Permission groups)
-- ============================================================================

CREATE TABLE IF NOT EXISTS role (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    type VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    permissions CLOB NOT NULL DEFAULT '[]',
    is_system BOOLEAN NOT NULL DEFAULT FALSE,
    can_approve BOOLEAN NOT NULL DEFAULT FALSE,
    can_insert BOOLEAN NOT NULL DEFAULT FALSE,
    can_update BOOLEAN NOT NULL DEFAULT FALSE,
    can_delete BOOLEAN NOT NULL DEFAULT FALSE,
    requires_approval BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_role_account FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE,
    CONSTRAINT uk_role_account_type UNIQUE (account_id, type)
);

CREATE INDEX idx_role_account ON role(account_id);
CREATE INDEX idx_role_type ON role(type);

-- ============================================================================
-- USER
-- ============================================================================

CREATE TABLE IF NOT EXISTS "user" (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(500),
    phone VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING_ACTIVATION',
    email_verified_at TIMESTAMP,
    last_login_at TIMESTAMP,
    failed_login_attempts INT NOT NULL DEFAULT 0,
    locked_until TIMESTAMP,
    preferences CLOB DEFAULT '{}',
    timezone VARCHAR(50) NOT NULL DEFAULT 'America/Sao_Paulo',
    locale VARCHAR(10) NOT NULL DEFAULT 'pt-BR',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_account FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_email_account UNIQUE (account_id, email)
);

CREATE INDEX idx_user_account ON "user"(account_id);
CREATE INDEX idx_user_email ON "user"(email);
CREATE INDEX idx_user_status ON "user"(status);

-- ============================================================================
-- USER_ROLE (Many-to-Many)
-- ============================================================================

CREATE TABLE IF NOT EXISTS user_role (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    assigned_by UUID,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_role UNIQUE (user_id, role_id)
);

CREATE INDEX idx_user_role_user ON user_role(user_id);
CREATE INDEX idx_user_role_role ON user_role(role_id);

-- ============================================================================
-- APPROVAL_REQUEST (PR-like workflow)
-- ============================================================================

CREATE TABLE IF NOT EXISTS approval_request (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID,
    operation VARCHAR(20) NOT NULL,
    data_before CLOB,
    data_after CLOB NOT NULL,
    requested_by UUID NOT NULL,
    requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reason VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reviewed_by UUID,
    reviewed_at TIMESTAMP,
    review_comment VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_approval_account FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE,
    CONSTRAINT fk_approval_requested_by FOREIGN KEY (requested_by) REFERENCES "user"(id),
    CONSTRAINT fk_approval_reviewed_by FOREIGN KEY (reviewed_by) REFERENCES "user"(id)
);

CREATE INDEX idx_approval_account ON approval_request(account_id);
CREATE INDEX idx_approval_status ON approval_request(status);
CREATE INDEX idx_approval_entity ON approval_request(entity_type, entity_id);

-- ============================================================================
-- AUDIT_LOG
-- ============================================================================

CREATE TABLE IF NOT EXISTS audit_log (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    user_id UUID,
    user_email VARCHAR(255),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id UUID,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    request_id UUID,
    old_data CLOB,
    new_data CLOB,
    metadata CLOB DEFAULT '{}',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_account FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE
);

CREATE INDEX idx_audit_account ON audit_log(account_id);
CREATE INDEX idx_audit_user ON audit_log(user_id);
CREATE INDEX idx_audit_created ON audit_log(created_at);

-- ============================================================================
-- PROGRAMA_MILHAS (Reference data)
-- ============================================================================

CREATE TABLE IF NOT EXISTS programa_milhas (
    id UUID PRIMARY KEY,
    account_id UUID,
    brand VARCHAR(100) NOT NULL,
    nome_completo VARCHAR(255),
    logo_url VARCHAR(500),
    website_url VARCHAR(500),
    moeda VARCHAR(3) NOT NULL DEFAULT 'BRL',
    tipo VARCHAR(50) NOT NULL DEFAULT 'AIRLINE',
    pais VARCHAR(2) NOT NULL DEFAULT 'BR',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_programa_account FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE
);

CREATE INDEX idx_programa_account ON programa_milhas(account_id);
CREATE INDEX idx_programa_brand ON programa_milhas(brand);

-- ============================================================================
-- CLIENTE
-- ============================================================================

CREATE TABLE IF NOT EXISTS cliente (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    titular_id UUID,                              -- NULL = titular, NOT NULL = dependente
    parentesco VARCHAR(20),                       -- CONJUGE, FILHO, FILHA, PAI, MAE, OUTRO
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    cpf VARCHAR(14),
    rg VARCHAR(20),
    telefone VARCHAR(20),
    celular VARCHAR(20),
    endereco_logradouro VARCHAR(255),
    endereco_numero VARCHAR(20),
    endereco_complemento VARCHAR(100),
    endereco_bairro VARCHAR(100),
    endereco_cidade VARCHAR(100),
    endereco_estado VARCHAR(2),
    endereco_cep VARCHAR(10),
    data_nascimento DATE,
    sexo VARCHAR(1),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    approval_status VARCHAR(20) NOT NULL DEFAULT 'AUTO_APPROVED',
    created_by UUID,
    approved_by UUID,
    approved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cliente_account FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE,
    CONSTRAINT fk_cliente_titular FOREIGN KEY (titular_id) REFERENCES cliente(id) ON DELETE CASCADE
);

CREATE INDEX idx_cliente_account ON cliente(account_id);
CREATE INDEX idx_cliente_nome ON cliente(nome);
CREATE INDEX idx_cliente_titular ON cliente(titular_id);
CREATE INDEX idx_cliente_approval ON cliente(account_id, approval_status);

-- ============================================================================
-- CONTA_PROGRAMA (Miles account)
-- Uses tenant_id for backward compatibility with existing code
-- ============================================================================

CREATE TABLE IF NOT EXISTS conta_programa (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    cliente_id UUID,
    programa_id UUID NOT NULL,
    programa_nome VARCHAR(100) NOT NULL,
    owner VARCHAR(255) NOT NULL,
    saldo_milhas BIGINT NOT NULL DEFAULT 0,
    custo_base_total_brl DECIMAL(15,2) NOT NULL DEFAULT 0,
    custo_medio_milheiro_atual DECIMAL(15,6) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    approval_status VARCHAR(20) NOT NULL DEFAULT 'AUTO_APPROVED',
    created_by UUID,
    approved_by UUID,
    approved_at TIMESTAMP,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_conta_tenant FOREIGN KEY (tenant_id) REFERENCES account(id) ON DELETE CASCADE,
    CONSTRAINT fk_conta_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE SET NULL,
    CONSTRAINT fk_conta_programa FOREIGN KEY (programa_id) REFERENCES programa_milhas(id),
    CONSTRAINT uk_conta_owner UNIQUE (tenant_id, programa_id, owner)
);

CREATE INDEX idx_conta_tenant ON conta_programa(tenant_id);
CREATE INDEX idx_conta_owner ON conta_programa(owner);
CREATE INDEX idx_conta_approval ON conta_programa(tenant_id, approval_status);

-- ============================================================================
-- TRANSACAO
-- ============================================================================

CREATE TABLE IF NOT EXISTS transacao (
    id UUID PRIMARY KEY,
    conta_programa_id UUID NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    milhas BIGINT NOT NULL,
    valor_brl DECIMAL(15,2) NOT NULL DEFAULT 0,
    fonte VARCHAR(100),
    observacao VARCHAR(500),
    data TIMESTAMP NOT NULL,
    approval_status VARCHAR(20) NOT NULL DEFAULT 'AUTO_APPROVED',
    created_by UUID,
    approved_by UUID,
    approved_at TIMESTAMP,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transacao_conta FOREIGN KEY (conta_programa_id) REFERENCES conta_programa(id)
);

CREATE INDEX idx_transacao_conta ON transacao(conta_programa_id);
CREATE INDEX idx_transacao_data ON transacao(data);

-- ============================================================================
-- SALDO_MILHAS (Legacy/Simple table)
-- ============================================================================

CREATE TABLE IF NOT EXISTS saldo_milhas (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    programa VARCHAR(100) NOT NULL,
    owner VARCHAR(100) NOT NULL,
    quantidade BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_saldo_milhas UNIQUE (tenant_id, programa, owner),
    CONSTRAINT fk_saldo_tenant FOREIGN KEY (tenant_id) REFERENCES account(id)
);

CREATE INDEX idx_saldo_milhas_tenant ON saldo_milhas(tenant_id);

-- ============================================================================
-- VIAGEM
-- ============================================================================

CREATE TABLE IF NOT EXISTS viagem (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    cliente_id UUID,
    destino VARCHAR(255) NOT NULL,
    origem VARCHAR(255),
    data_ida DATE NOT NULL,
    data_volta DATE,
    valor_total DECIMAL(15,2),
    milhas_utilizadas BIGINT,
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNED',
    observacoes VARCHAR(1000),
    approval_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_by UUID NOT NULL,
    approved_by UUID,
    approved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_viagem_account FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE,
    CONSTRAINT fk_viagem_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE SET NULL,
    CONSTRAINT fk_viagem_created_by FOREIGN KEY (created_by) REFERENCES "user"(id),
    CONSTRAINT fk_viagem_approved_by FOREIGN KEY (approved_by) REFERENCES "user"(id)
);

CREATE INDEX idx_viagem_account ON viagem(account_id);
CREATE INDEX idx_viagem_data ON viagem(data_ida);

-- ============================================================================
-- SEED DATA: Test Account
-- ============================================================================

-- Create test account
INSERT INTO account (id, name, slug, cnpj, email, phone, status, max_users, plan, trial_ends_at)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'Vanessa Viagem',
    'vanessa-viagem',
    '12345678000199',
    'contato@vanessaviagem.com.br',
    '11999999999',
    'ACTIVE',
    10,
    'PREMIUM',
    NULL
);

-- ============================================================================
-- SEED DATA: Roles for Test Account
-- ============================================================================

-- ROOT role
INSERT INTO role (id, account_id, type, name, description, permissions, is_system, can_approve, can_insert, can_update, can_delete, requires_approval)
VALUES (
    '10000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000001',
    'ROOT',
    'Root',
    'Super administrador com controle total do sistema',
    '["*"]',
    TRUE,
    TRUE,
    TRUE,
    TRUE,
    TRUE,
    FALSE
);

-- ADMIN role
INSERT INTO role (id, account_id, type, name, description, permissions, is_system, can_approve, can_insert, can_update, can_delete, requires_approval)
VALUES (
    '10000000-0000-0000-0000-000000000002',
    '00000000-0000-0000-0000-000000000001',
    'ADMIN',
    'Administrador',
    'Pode gerenciar usuarios e aprovar alteracoes',
    '["users:read", "users:write", "data:read", "data:write", "data:delete", "approvals:manage"]',
    TRUE,
    TRUE,
    TRUE,
    TRUE,
    TRUE,
    FALSE
);

-- MANAGER role
INSERT INTO role (id, account_id, type, name, description, permissions, is_system, can_approve, can_insert, can_update, can_delete, requires_approval)
VALUES (
    '10000000-0000-0000-0000-000000000003',
    '00000000-0000-0000-0000-000000000001',
    'MANAGER',
    'Gerente',
    'Pode inserir e atualizar dados. Alteracoes requerem aprovacao',
    '["data:read", "data:write:pending", "reports:read"]',
    TRUE,
    FALSE,
    TRUE,
    TRUE,
    FALSE,
    TRUE
);

-- OPERATOR role
INSERT INTO role (id, account_id, type, name, description, permissions, is_system, can_approve, can_insert, can_update, can_delete, requires_approval)
VALUES (
    '10000000-0000-0000-0000-000000000004',
    '00000000-0000-0000-0000-000000000001',
    'OPERATOR',
    'Operador',
    'Pode apenas inserir dados. Alteracoes requerem aprovacao',
    '["data:read", "data:insert:pending"]',
    TRUE,
    FALSE,
    TRUE,
    FALSE,
    FALSE,
    TRUE
);

-- VIEWER role
INSERT INTO role (id, account_id, type, name, description, permissions, is_system, can_approve, can_insert, can_update, can_delete, requires_approval)
VALUES (
    '10000000-0000-0000-0000-000000000005',
    '00000000-0000-0000-0000-000000000001',
    'VIEWER',
    'Visualizador',
    'Apenas visualizacao. Nao pode fazer alteracoes',
    '["data:read"]',
    TRUE,
    FALSE,
    FALSE,
    FALSE,
    FALSE,
    TRUE
);

-- ============================================================================
-- SEED DATA: Test Users (one for each role)
-- Password for all: "senha123" (BCrypt hash)
-- ============================================================================

-- ROOT user
INSERT INTO "user" (id, account_id, email, password_hash, name, status, email_verified_at)
VALUES (
    '20000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000001',
    'root@vanessaviagem.com.br',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3aRYkjn4L7Q8IA5.c6Ky',
    'Usuario Root',
    'ACTIVE',
    CURRENT_TIMESTAMP
);

-- ADMIN user
INSERT INTO "user" (id, account_id, email, password_hash, name, status, email_verified_at)
VALUES (
    '20000000-0000-0000-0000-000000000002',
    '00000000-0000-0000-0000-000000000001',
    'admin@vanessaviagem.com.br',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3aRYkjn4L7Q8IA5.c6Ky',
    'Usuario Admin',
    'ACTIVE',
    CURRENT_TIMESTAMP
);

-- MANAGER user
INSERT INTO "user" (id, account_id, email, password_hash, name, status, email_verified_at)
VALUES (
    '20000000-0000-0000-0000-000000000003',
    '00000000-0000-0000-0000-000000000001',
    'manager@vanessaviagem.com.br',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3aRYkjn4L7Q8IA5.c6Ky',
    'Usuario Manager',
    'ACTIVE',
    CURRENT_TIMESTAMP
);

-- OPERATOR user
INSERT INTO "user" (id, account_id, email, password_hash, name, status, email_verified_at)
VALUES (
    '20000000-0000-0000-0000-000000000004',
    '00000000-0000-0000-0000-000000000001',
    'operator@vanessaviagem.com.br',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3aRYkjn4L7Q8IA5.c6Ky',
    'Usuario Operator',
    'ACTIVE',
    CURRENT_TIMESTAMP
);

-- VIEWER user
INSERT INTO "user" (id, account_id, email, password_hash, name, status, email_verified_at)
VALUES (
    '20000000-0000-0000-0000-000000000005',
    '00000000-0000-0000-0000-000000000001',
    'viewer@vanessaviagem.com.br',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3aRYkjn4L7Q8IA5.c6Ky',
    'Usuario Viewer',
    'ACTIVE',
    CURRENT_TIMESTAMP
);

-- ============================================================================
-- SEED DATA: Assign Roles to Users
-- ============================================================================

INSERT INTO user_role (id, user_id, role_id, assigned_at)
VALUES ('30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP);

INSERT INTO user_role (id, user_id, role_id, assigned_at)
VALUES ('30000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002', CURRENT_TIMESTAMP);

INSERT INTO user_role (id, user_id, role_id, assigned_at)
VALUES ('30000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000003', CURRENT_TIMESTAMP);

INSERT INTO user_role (id, user_id, role_id, assigned_at)
VALUES ('30000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000004', CURRENT_TIMESTAMP);

INSERT INTO user_role (id, user_id, role_id, assigned_at)
VALUES ('30000000-0000-0000-0000-000000000005', '20000000-0000-0000-0000-000000000005', '10000000-0000-0000-0000-000000000005', CURRENT_TIMESTAMP);

-- ============================================================================
-- SEED DATA: Global Programs (no account_id = shared)
-- ============================================================================

INSERT INTO programa_milhas (id, account_id, brand, nome_completo, tipo, pais, status) VALUES
    ('40000000-0000-0000-0000-000000000001', NULL, 'Smiles', 'Smiles GOL', 'AIRLINE', 'BR', 'ACTIVE'),
    ('40000000-0000-0000-0000-000000000002', NULL, 'LATAM Pass', 'LATAM Pass', 'AIRLINE', 'BR', 'ACTIVE'),
    ('40000000-0000-0000-0000-000000000003', NULL, 'Azul Fidelidade', 'TudoAzul', 'AIRLINE', 'BR', 'ACTIVE'),
    ('40000000-0000-0000-0000-000000000004', NULL, 'Livelo', 'Livelo', 'BANK', 'BR', 'ACTIVE'),
    ('40000000-0000-0000-0000-000000000005', NULL, 'Esfera', 'Esfera Santander', 'BANK', 'BR', 'ACTIVE'),
    ('40000000-0000-0000-0000-000000000006', NULL, 'AAdvantage', 'American Airlines AAdvantage', 'AIRLINE', 'US', 'ACTIVE'),
    ('40000000-0000-0000-0000-000000000007', NULL, 'MileagePlus', 'United MileagePlus', 'AIRLINE', 'US', 'ACTIVE'),
    ('40000000-0000-0000-0000-000000000008', NULL, 'SkyMiles', 'Delta SkyMiles', 'AIRLINE', 'US', 'ACTIVE'),
    ('40000000-0000-0000-0000-000000000009', NULL, 'Flying Blue', 'Air France-KLM Flying Blue', 'AIRLINE', 'FR', 'ACTIVE'),
    ('40000000-0000-0000-0000-000000000010', NULL, 'TAP Miles&Go', 'TAP Miles&Go', 'AIRLINE', 'PT', 'ACTIVE');

-- ============================================================================
-- SEED DATA: Sample Clients - TITULARES E DEPENDENTES
-- ============================================================================

-- TITULAR 1: Joao Silva (com esposa e 2 filhos)
INSERT INTO cliente (id, account_id, titular_id, parentesco, nome, email, cpf, celular, data_nascimento, sexo, status, approval_status, created_by, approved_by, approved_at)
VALUES (
    '50000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000001',
    NULL,                                         -- TITULAR (sem titular_id)
    NULL,                                         -- TITULAR nao tem parentesco
    'Joao Silva',
    'joao.silva@email.com',
    '123.456.789-00',
    '11988887777',
    '1980-05-15',
    'M',
    'ACTIVE',
    'AUTO_APPROVED',
    '20000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000002',
    CURRENT_TIMESTAMP
);

-- DEPENDENTE 1.1: Ana Silva (esposa do Joao)
INSERT INTO cliente (id, account_id, titular_id, parentesco, nome, email, cpf, celular, data_nascimento, sexo, status, approval_status, created_by, approved_by, approved_at)
VALUES (
    '50000000-0000-0000-0000-000000000011',
    '00000000-0000-0000-0000-000000000001',
    '50000000-0000-0000-0000-000000000001',    -- Dependente do Joao
    'CONJUGE',
    'Ana Silva',
    'ana.silva@email.com',
    '123.456.789-01',
    '11988887778',
    '1982-08-20',
    'F',
    'ACTIVE',
    'AUTO_APPROVED',
    '20000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000002',
    CURRENT_TIMESTAMP
);

-- DEPENDENTE 1.2: Pedro Silva (filho do Joao)
INSERT INTO cliente (id, account_id, titular_id, parentesco, nome, email, cpf, celular, data_nascimento, sexo, status, approval_status, created_by, approved_by, approved_at)
VALUES (
    '50000000-0000-0000-0000-000000000012',
    '00000000-0000-0000-0000-000000000001',
    '50000000-0000-0000-0000-000000000001',    -- Dependente do Joao
    'FILHO',
    'Pedro Silva',
    NULL,                                         -- Menor de idade, sem email
    NULL,                                         -- Menor de idade, sem CPF
    NULL,
    '2010-03-10',
    'M',
    'ACTIVE',
    'AUTO_APPROVED',
    '20000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000002',
    CURRENT_TIMESTAMP
);

-- DEPENDENTE 1.3: Julia Silva (filha do Joao)
INSERT INTO cliente (id, account_id, titular_id, parentesco, nome, email, cpf, celular, data_nascimento, sexo, status, approval_status, created_by, approved_by, approved_at)
VALUES (
    '50000000-0000-0000-0000-000000000013',
    '00000000-0000-0000-0000-000000000001',
    '50000000-0000-0000-0000-000000000001',    -- Dependente do Joao
    'FILHA',
    'Julia Silva',
    NULL,                                         -- Menor de idade, sem email
    NULL,                                         -- Menor de idade, sem CPF
    NULL,
    '2015-11-25',
    'F',
    'ACTIVE',
    'AUTO_APPROVED',
    '20000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000002',
    CURRENT_TIMESTAMP
);

-- TITULAR 2: Maria Santos (sem dependentes)
INSERT INTO cliente (id, account_id, titular_id, parentesco, nome, email, cpf, celular, data_nascimento, sexo, status, approval_status, created_by, approved_by, approved_at)
VALUES (
    '50000000-0000-0000-0000-000000000002',
    '00000000-0000-0000-0000-000000000001',
    NULL,                                         -- TITULAR
    NULL,
    'Maria Santos',
    'maria.santos@email.com',
    '987.654.321-00',
    '11977776666',
    '1975-12-01',
    'F',
    'ACTIVE',
    'AUTO_APPROVED',
    '20000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000002',
    CURRENT_TIMESTAMP
);

-- ============================================================================
-- SEED DATA: Sample Conta Programa (miles accounts)
-- Note: Uses tenant_id for backward compatibility
-- ============================================================================

INSERT INTO conta_programa (id, tenant_id, cliente_id, programa_id, programa_nome, owner, saldo_milhas, custo_base_total_brl, custo_medio_milheiro_atual, status, approval_status, created_by, approved_by, approved_at)
VALUES (
    '60000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000001',
    '50000000-0000-0000-0000-000000000001',
    '40000000-0000-0000-0000-000000000001',
    'Smiles',
    'Joao Silva',
    50000,
    1250.00,
    25.00,
    'ACTIVE',
    'AUTO_APPROVED',
    '20000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000002',
    CURRENT_TIMESTAMP
);

INSERT INTO conta_programa (id, tenant_id, cliente_id, programa_id, programa_nome, owner, saldo_milhas, custo_base_total_brl, custo_medio_milheiro_atual, status, approval_status, created_by, approved_by, approved_at)
VALUES (
    '60000000-0000-0000-0000-000000000002',
    '00000000-0000-0000-0000-000000000001',
    '50000000-0000-0000-0000-000000000002',
    '40000000-0000-0000-0000-000000000002',
    'LATAM Pass',
    'Maria Santos',
    30000,
    900.00,
    30.00,
    'ACTIVE',
    'AUTO_APPROVED',
    '20000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000002',
    CURRENT_TIMESTAMP
);

-- ============================================================================
-- SEED DATA: Sample Pending Approval (created by operator)
-- ============================================================================

-- TITULAR 3: Carlos Pereira (pendente aprovacao, sem dependentes ainda)
INSERT INTO cliente (id, account_id, titular_id, parentesco, nome, email, cpf, celular, data_nascimento, sexo, status, approval_status, created_by)
VALUES (
    '50000000-0000-0000-0000-000000000003',
    '00000000-0000-0000-0000-000000000001',
    NULL,                                         -- TITULAR
    NULL,
    'Carlos Pereira',
    'carlos.pereira@email.com',
    '111.222.333-44',
    '11966665555',
    '1990-07-22',
    'M',
    'ACTIVE',
    'PENDING',
    '20000000-0000-0000-0000-000000000004'
);

-- Create approval request for the pending client
INSERT INTO approval_request (id, account_id, entity_type, entity_id, operation, data_after, requested_by, status)
VALUES (
    '70000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000001',
    'CLIENTE',
    '50000000-0000-0000-0000-000000000003',
    'INSERT',
    '{"nome": "Carlos Pereira", "email": "carlos.pereira@email.com", "cpf": "111.222.333-44"}',
    '20000000-0000-0000-0000-000000000004',
    'PENDING'
);

-- ============================================================================
-- SEED DATA: Transacoes de Milhas (historico)
-- ============================================================================

-- Joao Silva - Smiles (conta 60000000-0000-0000-0000-000000000001)
-- Compra inicial de 30.000 milhas por R$ 750 (R$ 25/milheiro)
INSERT INTO transacao (id, conta_programa_id, tipo, milhas, valor_brl, fonte, observacao, data, approval_status, created_by, approved_by, approved_at, criado_em)
VALUES (
    '80000000-0000-0000-0000-000000000001',
    '60000000-0000-0000-0000-000000000001',
    'COMPRA',
    30000,
    750.00,
    'Smiles - Promocao Black Friday',
    'Compra direta no site Smiles com 40% de bonus',
    '2024-11-25 10:30:00',
    'AUTO_APPROVED',
    '20000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000002',
    '2024-11-25 10:30:00',
    '2024-11-25 10:30:00'
);

-- Bonus de 12.000 milhas (40% da compra)
INSERT INTO transacao (id, conta_programa_id, tipo, milhas, valor_brl, fonte, observacao, data, approval_status, created_by, approved_by, approved_at, criado_em)
VALUES (
    '80000000-0000-0000-0000-000000000002',
    '60000000-0000-0000-0000-000000000001',
    'BONUS',
    12000,
    0.00,
    'Smiles - Bonus Black Friday 40%',
    'Bonus automatico da promocao',
    '2024-11-25 10:30:00',
    'AUTO_APPROVED',
    '20000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000002',
    '2024-11-25 10:30:00',
    '2024-11-25 10:30:00'
);

-- Compra adicional de 10.000 milhas por R$ 300 (R$ 30/milheiro)
INSERT INTO transacao (id, conta_programa_id, tipo, milhas, valor_brl, fonte, observacao, data, approval_status, created_by, approved_by, approved_at, criado_em)
VALUES (
    '80000000-0000-0000-0000-000000000003',
    '60000000-0000-0000-0000-000000000001',
    'COMPRA',
    10000,
    300.00,
    'Smiles - Compra Avulsa',
    'Reforco para viagem de janeiro',
    '2024-12-15 14:20:00',
    'AUTO_APPROVED',
    '20000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000002',
    '2024-12-15 14:20:00',
    '2024-12-15 14:20:00'
);

-- Venda de 2.000 milhas por R$ 60 (R$ 30/milheiro - lucro!)
INSERT INTO transacao (id, conta_programa_id, tipo, milhas, valor_brl, fonte, observacao, data, approval_status, created_by, approved_by, approved_at, criado_em)
VALUES (
    '80000000-0000-0000-0000-000000000004',
    '60000000-0000-0000-0000-000000000001',
    'VENDA',
    2000,
    60.00,
    'MaxMilhas',
    'Venda para cliente via MaxMilhas',
    '2024-12-20 09:15:00',
    'AUTO_APPROVED',
    '20000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000002',
    '2024-12-20 09:15:00',
    '2024-12-20 09:15:00'
);

-- Maria Santos - LATAM Pass (conta 60000000-0000-0000-0000-000000000002)
-- Compra inicial de 20.000 milhas por R$ 600 (R$ 30/milheiro)
INSERT INTO transacao (id, conta_programa_id, tipo, milhas, valor_brl, fonte, observacao, data, approval_status, created_by, approved_by, approved_at, criado_em)
VALUES (
    '80000000-0000-0000-0000-000000000005',
    '60000000-0000-0000-0000-000000000002',
    'COMPRA',
    20000,
    600.00,
    'LATAM Pass - Clube LATAM',
    'Assinatura mensal Clube LATAM',
    '2024-10-01 08:00:00',
    'AUTO_APPROVED',
    '20000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000002',
    '2024-10-01 08:00:00',
    '2024-10-01 08:00:00'
);

-- Bonus cartao de credito
INSERT INTO transacao (id, conta_programa_id, tipo, milhas, valor_brl, fonte, observacao, data, approval_status, created_by, approved_by, approved_at, criado_em)
VALUES (
    '80000000-0000-0000-0000-000000000006',
    '60000000-0000-0000-0000-000000000002',
    'BONUS',
    5000,
    0.00,
    'Itau Personnalite - Cashback',
    'Pontos convertidos do cartao Itau',
    '2024-11-05 12:00:00',
    'AUTO_APPROVED',
    '20000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000002',
    '2024-11-05 12:00:00',
    '2024-11-05 12:00:00'
);

-- Compra adicional
INSERT INTO transacao (id, conta_programa_id, tipo, milhas, valor_brl, fonte, observacao, data, approval_status, created_by, approved_by, approved_at, criado_em)
VALUES (
    '80000000-0000-0000-0000-000000000007',
    '60000000-0000-0000-0000-000000000002',
    'COMPRA',
    10000,
    300.00,
    'LATAM Pass - Promocao Verao',
    'Promocao de verao 2025',
    '2025-01-10 16:45:00',
    'AUTO_APPROVED',
    '20000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000002',
    '2025-01-10 16:45:00',
    '2025-01-10 16:45:00'
);

-- Venda parcial
INSERT INTO transacao (id, conta_programa_id, tipo, milhas, valor_brl, fonte, observacao, data, approval_status, created_by, approved_by, approved_at, criado_em)
VALUES (
    '80000000-0000-0000-0000-000000000008',
    '60000000-0000-0000-0000-000000000002',
    'VENDA',
    5000,
    175.00,
    'Hotmilhas',
    'Venda para Hotmilhas - cotacao R$ 35/milheiro',
    '2025-01-15 11:30:00',
    'AUTO_APPROVED',
    '20000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000002',
    '2025-01-15 11:30:00',
    '2025-01-15 11:30:00'
);

-- Transacao PENDENTE (criada por OPERATOR, aguardando aprovacao)
INSERT INTO transacao (id, conta_programa_id, tipo, milhas, valor_brl, fonte, observacao, data, approval_status, created_by, criado_em)
VALUES (
    '80000000-0000-0000-0000-000000000009',
    '60000000-0000-0000-0000-000000000001',
    'COMPRA',
    15000,
    375.00,
    'Smiles - Promocao Janeiro',
    'Aguardando aprovacao do admin',
    '2025-01-28 10:00:00',
    'PENDING',
    '20000000-0000-0000-0000-000000000004',
    '2025-01-28 10:00:00'
);

-- Approval request para transacao pendente
INSERT INTO approval_request (id, account_id, entity_type, entity_id, operation, data_after, requested_by, status, reason)
VALUES (
    '70000000-0000-0000-0000-000000000002',
    '00000000-0000-0000-0000-000000000001',
    'TRANSACAO',
    '80000000-0000-0000-0000-000000000009',
    'INSERT',
    '{"tipo": "COMPRA", "milhas": 15000, "valor_brl": 375.00, "fonte": "Smiles - Promocao Janeiro"}',
    '20000000-0000-0000-0000-000000000004',
    'PENDING',
    'Promocao imperdivel, precisa aprovar rapido!'
);

-- ============================================================================
-- SEED DATA: Viagens
-- ============================================================================

-- Viagem 1: Completa (ja realizada)
INSERT INTO viagem (id, account_id, cliente_id, destino, origem, data_ida, data_volta, valor_total, milhas_utilizadas, status, observacoes, approval_status, created_by, approved_by, approved_at)
VALUES (
    '90000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000001',
    '50000000-0000-0000-0000-000000000001',
    'Miami, FL - Estados Unidos',
    'Sao Paulo, SP - GRU',
    '2024-12-15',
    '2024-12-22',
    8500.00,
    45000,
    'COMPLETED',
    'Ferias de fim de ano. Voo GOL com Smiles. Hotel Marriott Brickell.',
    'AUTO_APPROVED',
    '20000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000002',
    '2024-11-20 14:00:00'
);

-- Viagem 2: Confirmada (proxima viagem)
INSERT INTO viagem (id, account_id, cliente_id, destino, origem, data_ida, data_volta, valor_total, milhas_utilizadas, status, observacoes, approval_status, created_by, approved_by, approved_at)
VALUES (
    '90000000-0000-0000-0000-000000000002',
    '00000000-0000-0000-0000-000000000001',
    '50000000-0000-0000-0000-000000000002',
    'Lisboa, Portugal',
    'Sao Paulo, SP - GRU',
    '2025-03-10',
    '2025-03-20',
    12000.00,
    60000,
    'CONFIRMED',
    'Viagem de negocios + lazer. Voo LATAM. Hotel em Alfama.',
    'AUTO_APPROVED',
    '20000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000002',
    '2025-01-10 09:30:00'
);

-- Viagem 3: Planejada (ainda nao confirmada)
INSERT INTO viagem (id, account_id, cliente_id, destino, origem, data_ida, data_volta, valor_total, milhas_utilizadas, status, observacoes, approval_status, created_by, approved_by, approved_at)
VALUES (
    '90000000-0000-0000-0000-000000000003',
    '00000000-0000-0000-0000-000000000001',
    '50000000-0000-0000-0000-000000000001',
    'Paris, Franca',
    'Sao Paulo, SP - GRU',
    '2025-07-15',
    '2025-07-30',
    NULL,
    NULL,
    'PLANNED',
    'Ferias de julho. Pesquisando melhores opcoes de voo e hotel.',
    'AUTO_APPROVED',
    '20000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000002',
    '2025-01-20 16:00:00'
);

-- Viagem 4: Nacional (sem milhas)
INSERT INTO viagem (id, account_id, cliente_id, destino, origem, data_ida, data_volta, valor_total, milhas_utilizadas, status, observacoes, approval_status, created_by, approved_by, approved_at)
VALUES (
    '90000000-0000-0000-0000-000000000004',
    '00000000-0000-0000-0000-000000000001',
    '50000000-0000-0000-0000-000000000002',
    'Rio de Janeiro, RJ - SDU',
    'Sao Paulo, SP - CGH',
    '2025-02-14',
    '2025-02-16',
    1200.00,
    0,
    'CONFIRMED',
    'Final de semana romantico. Voo pago, sem uso de milhas.',
    'AUTO_APPROVED',
    '20000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000002',
    '2025-01-25 11:00:00'
);

-- Viagem 5: PENDENTE (criada por MANAGER, aguardando aprovacao)
INSERT INTO viagem (id, account_id, cliente_id, destino, origem, data_ida, data_volta, valor_total, milhas_utilizadas, status, observacoes, approval_status, created_by)
VALUES (
    '90000000-0000-0000-0000-000000000005',
    '00000000-0000-0000-0000-000000000001',
    '50000000-0000-0000-0000-000000000001',
    'Cancun, Mexico',
    'Sao Paulo, SP - GRU',
    '2025-04-20',
    '2025-04-27',
    9500.00,
    50000,
    'PLANNED',
    'Semana de pascoa em Cancun. Aguardando aprovacao para emitir.',
    'PENDING',
    '20000000-0000-0000-0000-000000000003'
);

-- Approval request para viagem pendente
INSERT INTO approval_request (id, account_id, entity_type, entity_id, operation, data_after, requested_by, status, reason)
VALUES (
    '70000000-0000-0000-0000-000000000003',
    '00000000-0000-0000-0000-000000000001',
    'VIAGEM',
    '90000000-0000-0000-0000-000000000005',
    'INSERT',
    '{"destino": "Cancun, Mexico", "data_ida": "2025-04-20", "milhas": 50000, "valor": 9500.00}',
    '20000000-0000-0000-0000-000000000003',
    'PENDING',
    'Cliente quer aproveitar promocao de pascoa'
);

-- ============================================================================
-- Legacy compatibility: tenant table as alias for account
-- ============================================================================

CREATE VIEW tenant AS SELECT id, name as nome, cnpj, status, created_at as criado_em, updated_at as atualizado_em FROM account;

-- ============================================================================
-- SUMMARY: Test Users for Each Role
-- ============================================================================
-- Email                           | Password  | Role     | Can Approve | Requires Approval
-- --------------------------------|-----------|----------|-------------|-------------------
-- root@vanessaviagem.com.br       | senha123  | ROOT     | Yes         | No
-- admin@vanessaviagem.com.br      | senha123  | ADMIN    | Yes         | No
-- manager@vanessaviagem.com.br    | senha123  | MANAGER  | No          | Yes
-- operator@vanessaviagem.com.br   | senha123  | OPERATOR | No          | Yes
-- viewer@vanessaviagem.com.br     | senha123  | VIEWER   | No          | Yes (read-only)
-- ============================================================================
