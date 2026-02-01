-- ============================================================================
-- V004: Additional Dev Accounts for Multi-Tenant Testing
-- ============================================================================
-- Creates additional accounts/tenants for testing tenant isolation in DEV mode.
-- Each account has its own set of test data.
-- Uses the NEW cliente schema from V003 (tenant_id, tipo, nome, sobrenome, etc.)
-- ============================================================================

-- ============================================================================
-- ACCOUNT 2: Milhas Express
-- ============================================================================

INSERT INTO account (id, name, slug, cnpj, email, phone, status, max_users, plan, trial_ends_at)
VALUES (
    '00000000-0000-0000-0000-000000000002',
    'Milhas Express',
    'milhas-express',
    '98765432000188',
    'contato@milhasexpress.com.br',
    '21988888888',
    'ACTIVE',
    5,
    'BUSINESS',
    NULL
);

-- Roles for Account 2
INSERT INTO role (id, account_id, type, name, description, permissions, is_system, can_approve, can_insert, can_update, can_delete, requires_approval)
VALUES
    ('10000000-0000-0000-0000-000000000011', '00000000-0000-0000-0000-000000000002', 'ROOT', 'Root', 'Super administrador', '["*"]', TRUE, TRUE, TRUE, TRUE, TRUE, FALSE),
    ('10000000-0000-0000-0000-000000000012', '00000000-0000-0000-0000-000000000002', 'ADMIN', 'Administrador', 'Administrador geral', '["users:read", "users:write", "data:*"]', TRUE, TRUE, TRUE, TRUE, TRUE, FALSE),
    ('10000000-0000-0000-0000-000000000013', '00000000-0000-0000-0000-000000000002', 'MANAGER', 'Gerente', 'Gerente de operacoes', '["data:read", "data:write"]', TRUE, FALSE, TRUE, TRUE, FALSE, TRUE),
    ('10000000-0000-0000-0000-000000000014', '00000000-0000-0000-0000-000000000002', 'OPERATOR', 'Operador', 'Operador', '["data:read", "data:insert"]', TRUE, FALSE, TRUE, FALSE, FALSE, TRUE),
    ('10000000-0000-0000-0000-000000000015', '00000000-0000-0000-0000-000000000002', 'VIEWER', 'Visualizador', 'Apenas visualizacao', '["data:read"]', TRUE, FALSE, FALSE, FALSE, FALSE, TRUE);

-- Admin user for Account 2
INSERT INTO "user" (id, account_id, email, password_hash, name, status, email_verified_at)
VALUES (
    '20000000-0000-0000-0000-000000000011',
    '00000000-0000-0000-0000-000000000002',
    'admin@milhasexpress.com.br',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3aRYkjn4L7Q8IA5.c6Ky',
    'Admin Milhas Express',
    'ACTIVE',
    CURRENT_TIMESTAMP
);

INSERT INTO user_role (id, user_id, role_id, assigned_at)
VALUES ('30000000-0000-0000-0000-000000000011', '20000000-0000-0000-0000-000000000011', '10000000-0000-0000-0000-000000000012', CURRENT_TIMESTAMP);

-- Cliente for Account 2 (NEW SCHEMA: tenant_id, tipo, nome, sobrenome)
INSERT INTO cliente (id, tenant_id, tipo, nome, sobrenome, data_nascimento, sexo, ativo, observacoes)
VALUES (
    '50000000-0000-0000-0000-000000000101',
    '00000000-0000-0000-0000-000000000002',
    'TITULAR',
    'Roberto',
    'Almeida',
    '1985-03-20',
    'MASCULINO',
    TRUE,
    'Cliente principal da Milhas Express'
);

-- Documento para Roberto
INSERT INTO cliente_documento (id, cliente_id, tipo, numero, data_emissao, principal) VALUES
    ('d0000000-0000-0000-0000-000000000101', '50000000-0000-0000-0000-000000000101', 'CPF', '555.666.777-88', '2020-01-01', TRUE);

-- Contato para Roberto
INSERT INTO cliente_contato (id, cliente_id, tipo, valor, principal) VALUES
    ('c0000000-0000-0000-0000-000000000101', '50000000-0000-0000-0000-000000000101', 'EMAIL', 'roberto@milhasexpress.com', TRUE),
    ('c0000000-0000-0000-0000-000000000102', '50000000-0000-0000-0000-000000000101', 'CELULAR', '21977776666', FALSE);

-- Conta programa for Account 2
INSERT INTO conta_programa (id, tenant_id, cliente_id, programa_id, programa_nome, owner, saldo_milhas, custo_base_total_brl, custo_medio_milheiro_atual, status, approval_status, created_by, approved_by, approved_at)
VALUES (
    '60000000-0000-0000-0000-000000000101',
    '00000000-0000-0000-0000-000000000002',
    '50000000-0000-0000-0000-000000000101',
    '40000000-0000-0000-0000-000000000003',
    'Azul Fidelidade',
    'Roberto Almeida',
    75000,
    1500.00,
    20.00,
    'ACTIVE',
    'AUTO_APPROVED',
    '20000000-0000-0000-0000-000000000011',
    '20000000-0000-0000-0000-000000000011',
    CURRENT_TIMESTAMP
);

-- ============================================================================
-- ACCOUNT 3: Voando Alto Turismo
-- ============================================================================

INSERT INTO account (id, name, slug, cnpj, email, phone, status, max_users, plan, trial_ends_at)
VALUES (
    '00000000-0000-0000-0000-000000000003',
    'Voando Alto Turismo',
    'voando-alto',
    '11122233000144',
    'contato@voandoalto.com.br',
    '31977775555',
    'ACTIVE',
    3,
    'STARTER',
    NULL
);

-- Roles for Account 3
INSERT INTO role (id, account_id, type, name, description, permissions, is_system, can_approve, can_insert, can_update, can_delete, requires_approval)
VALUES
    ('10000000-0000-0000-0000-000000000021', '00000000-0000-0000-0000-000000000003', 'ROOT', 'Root', 'Super administrador', '["*"]', TRUE, TRUE, TRUE, TRUE, TRUE, FALSE),
    ('10000000-0000-0000-0000-000000000022', '00000000-0000-0000-0000-000000000003', 'ADMIN', 'Administrador', 'Administrador geral', '["users:read", "users:write", "data:*"]', TRUE, TRUE, TRUE, TRUE, TRUE, FALSE),
    ('10000000-0000-0000-0000-000000000023', '00000000-0000-0000-0000-000000000003', 'MANAGER', 'Gerente', 'Gerente de operacoes', '["data:read", "data:write"]', TRUE, FALSE, TRUE, TRUE, FALSE, TRUE),
    ('10000000-0000-0000-0000-000000000024', '00000000-0000-0000-0000-000000000003', 'OPERATOR', 'Operador', 'Operador', '["data:read", "data:insert"]', TRUE, FALSE, TRUE, FALSE, FALSE, TRUE),
    ('10000000-0000-0000-0000-000000000025', '00000000-0000-0000-0000-000000000003', 'VIEWER', 'Visualizador', 'Apenas visualizacao', '["data:read"]', TRUE, FALSE, FALSE, FALSE, FALSE, TRUE);

-- Admin user for Account 3
INSERT INTO "user" (id, account_id, email, password_hash, name, status, email_verified_at)
VALUES (
    '20000000-0000-0000-0000-000000000021',
    '00000000-0000-0000-0000-000000000003',
    'admin@voandoalto.com.br',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3aRYkjn4L7Q8IA5.c6Ky',
    'Admin Voando Alto',
    'ACTIVE',
    CURRENT_TIMESTAMP
);

INSERT INTO user_role (id, user_id, role_id, assigned_at)
VALUES ('30000000-0000-0000-0000-000000000021', '20000000-0000-0000-0000-000000000021', '10000000-0000-0000-0000-000000000022', CURRENT_TIMESTAMP);

-- Cliente for Account 3 (NEW SCHEMA)
INSERT INTO cliente (id, tenant_id, tipo, nome, sobrenome, data_nascimento, sexo, ativo, observacoes)
VALUES (
    '50000000-0000-0000-0000-000000000201',
    '00000000-0000-0000-0000-000000000003',
    'TITULAR',
    'Fernanda',
    'Costa',
    '1992-07-15',
    'FEMININO',
    TRUE,
    'Cliente da Voando Alto'
);

-- Documento para Fernanda
INSERT INTO cliente_documento (id, cliente_id, tipo, numero, data_emissao, principal) VALUES
    ('d0000000-0000-0000-0000-000000000201', '50000000-0000-0000-0000-000000000201', 'CPF', '999.888.777-66', '2020-01-01', TRUE);

-- Contato para Fernanda
INSERT INTO cliente_contato (id, cliente_id, tipo, valor, principal) VALUES
    ('c0000000-0000-0000-0000-000000000201', '50000000-0000-0000-0000-000000000201', 'EMAIL', 'fernanda@voandoalto.com', TRUE),
    ('c0000000-0000-0000-0000-000000000202', '50000000-0000-0000-0000-000000000201', 'CELULAR', '31966665544', FALSE);

-- Conta programa for Account 3
INSERT INTO conta_programa (id, tenant_id, cliente_id, programa_id, programa_nome, owner, saldo_milhas, custo_base_total_brl, custo_medio_milheiro_atual, status, approval_status, created_by, approved_by, approved_at)
VALUES (
    '60000000-0000-0000-0000-000000000201',
    '00000000-0000-0000-0000-000000000003',
    '50000000-0000-0000-0000-000000000201',
    '40000000-0000-0000-0000-000000000004',
    'Livelo',
    'Fernanda Costa',
    25000,
    500.00,
    20.00,
    'ACTIVE',
    'AUTO_APPROVED',
    '20000000-0000-0000-0000-000000000021',
    '20000000-0000-0000-0000-000000000021',
    CURRENT_TIMESTAMP
);

-- ============================================================================
-- ACCOUNT 4: Empty Account (for testing onboarding)
-- ============================================================================

INSERT INTO account (id, name, slug, cnpj, email, phone, status, max_users, plan, trial_ends_at)
VALUES (
    '00000000-0000-0000-0000-000000000004',
    'Empty Account',
    'empty-account',
    NULL,
    'empty@test.com',
    NULL,
    'TRIAL',
    2,
    'FREE',
    DATEADD('DAY', 30, CURRENT_TIMESTAMP)
);

-- Roles for Account 4
INSERT INTO role (id, account_id, type, name, description, permissions, is_system, can_approve, can_insert, can_update, can_delete, requires_approval)
VALUES
    ('10000000-0000-0000-0000-000000000031', '00000000-0000-0000-0000-000000000004', 'ROOT', 'Root', 'Super administrador', '["*"]', TRUE, TRUE, TRUE, TRUE, TRUE, FALSE),
    ('10000000-0000-0000-0000-000000000032', '00000000-0000-0000-0000-000000000004', 'ADMIN', 'Administrador', 'Administrador geral', '["users:read", "users:write", "data:*"]', TRUE, TRUE, TRUE, TRUE, TRUE, FALSE),
    ('10000000-0000-0000-0000-000000000033', '00000000-0000-0000-0000-000000000004', 'MANAGER', 'Gerente', 'Gerente de operacoes', '["data:read", "data:write"]', TRUE, FALSE, TRUE, TRUE, FALSE, TRUE),
    ('10000000-0000-0000-0000-000000000034', '00000000-0000-0000-0000-000000000004', 'OPERATOR', 'Operador', 'Operador', '["data:read", "data:insert"]', TRUE, FALSE, TRUE, FALSE, FALSE, TRUE),
    ('10000000-0000-0000-0000-000000000035', '00000000-0000-0000-0000-000000000004', 'VIEWER', 'Visualizador', 'Apenas visualizacao', '["data:read"]', TRUE, FALSE, FALSE, FALSE, FALSE, TRUE);

-- Admin user for Account 4
INSERT INTO "user" (id, account_id, email, password_hash, name, status, email_verified_at)
VALUES (
    '20000000-0000-0000-0000-000000000031',
    '00000000-0000-0000-0000-000000000004',
    'admin@empty.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3aRYkjn4L7Q8IA5.c6Ky',
    'Admin Empty',
    'ACTIVE',
    CURRENT_TIMESTAMP
);

INSERT INTO user_role (id, user_id, role_id, assigned_at)
VALUES ('30000000-0000-0000-0000-000000000031', '20000000-0000-0000-0000-000000000031', '10000000-0000-0000-0000-000000000032', CURRENT_TIMESTAMP);

-- No clients or data for Empty Account - used to test onboarding flow

-- ============================================================================
-- SUMMARY: Dev Accounts for Multi-Tenant Testing
-- ============================================================================
-- Account ID                             | Name                  | Plan     | Data
-- ---------------------------------------|----------------------|----------|------------------
-- 00000000-0000-0000-0000-000000000001   | Vanessa Viagem       | PREMIUM  | Full test data
-- 00000000-0000-0000-0000-000000000002   | Milhas Express       | BUSINESS | Partial data
-- 00000000-0000-0000-0000-000000000003   | Voando Alto Turismo  | STARTER  | Minimal data
-- 00000000-0000-0000-0000-000000000004   | Empty Account        | FREE     | No data (onboarding)
-- ============================================================================
