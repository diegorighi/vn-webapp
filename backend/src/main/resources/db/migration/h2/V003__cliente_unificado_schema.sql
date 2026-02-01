-- ============================================================================
-- V003: Cliente Unificado Schema (Substituindo cliente_module E cliente antigo)
-- ============================================================================
-- Account tem Clientes
-- Clientes podem ser TITULAR ou DEPENDENTE
-- Dependentes sempre vinculados a um Titular
-- Cascade delete: deletar Titular apaga todos os Dependentes
-- ============================================================================

-- ============================================================================
-- DROP TABELAS ANTIGAS (cliente_module e cliente antigo)
-- ============================================================================

-- Drop tabelas do cliente_module (V002)
DROP TABLE IF EXISTS cliente_contato CASCADE;
DROP TABLE IF EXISTS cliente_endereco CASCADE;
DROP TABLE IF EXISTS cliente_documento CASCADE;
DROP TABLE IF EXISTS cliente_module CASCADE;

-- Drop indices first (H2 quirk)
DROP INDEX IF EXISTS idx_viagem_account;
DROP INDEX IF EXISTS idx_viagem_data;
DROP INDEX IF EXISTS idx_transacao_conta;
DROP INDEX IF EXISTS idx_transacao_data;
DROP INDEX IF EXISTS idx_conta_tenant;
DROP INDEX IF EXISTS idx_conta_owner;
DROP INDEX IF EXISTS idx_conta_approval;
DROP INDEX IF EXISTS idx_cliente_account;
DROP INDEX IF EXISTS idx_cliente_nome;
DROP INDEX IF EXISTS idx_cliente_titular;
DROP INDEX IF EXISTS idx_cliente_approval;

-- Drop tabelas que referenciam o cliente antigo (V001)
DROP TABLE IF EXISTS viagem CASCADE;
DROP TABLE IF EXISTS transacao CASCADE;
DROP TABLE IF EXISTS conta_programa CASCADE;
DROP TABLE IF EXISTS cliente CASCADE;

-- ============================================================================
-- CLIENTE (Base para Titular e Dependente)
-- ============================================================================

CREATE TABLE cliente (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    tipo VARCHAR(20) NOT NULL,  -- TITULAR ou DEPENDENTE
    nome VARCHAR(100) NOT NULL,
    nome_do_meio VARCHAR(100),
    sobrenome VARCHAR(100) NOT NULL,
    data_nascimento DATE,
    sexo VARCHAR(20),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    observacoes VARCHAR(1000),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cliente_tenant FOREIGN KEY (tenant_id) REFERENCES account(id) ON DELETE CASCADE,
    CONSTRAINT ck_cliente_tipo CHECK (tipo IN ('TITULAR', 'DEPENDENTE'))
);

CREATE INDEX idx_cliente_tenant ON cliente(tenant_id);
CREATE INDEX idx_cliente_tipo ON cliente(tenant_id, tipo);
CREATE INDEX idx_cliente_nome ON cliente(nome, sobrenome);
CREATE INDEX idx_cliente_ativo ON cliente(tenant_id, ativo);

-- ============================================================================
-- CLIENTE_DEPENDENTE_VINCULO (Relacionamento Dependente -> Titular)
-- ============================================================================

CREATE TABLE cliente_dependente_vinculo (
    cliente_id UUID PRIMARY KEY,
    titular_id UUID NOT NULL,
    parentesco VARCHAR(20) NOT NULL,
    CONSTRAINT fk_dependente_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE,
    CONSTRAINT fk_dependente_titular FOREIGN KEY (titular_id) REFERENCES cliente(id) ON DELETE CASCADE,
    CONSTRAINT ck_parentesco CHECK (parentesco IN ('CONJUGE', 'FILHO', 'FILHA', 'PAI', 'MAE', 'IRMAO', 'IRMA', 'OUTRO'))
);

CREATE INDEX idx_dependente_titular ON cliente_dependente_vinculo(titular_id);

-- ============================================================================
-- CLIENTE_DOCUMENTO (Documentos do cliente)
-- ============================================================================

CREATE TABLE cliente_documento (
    id UUID PRIMARY KEY,
    cliente_id UUID NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    numero VARCHAR(50) NOT NULL,
    data_emissao DATE,
    data_validade DATE,
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    arquivo_url VARCHAR(500),
    nome_arquivo VARCHAR(255),
    CONSTRAINT fk_documento_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE,
    CONSTRAINT ck_documento_tipo CHECK (tipo IN ('CPF', 'RG', 'PASSAPORTE', 'CNH', 'OUTRO'))
);

CREATE INDEX idx_documento_cliente ON cliente_documento(cliente_id);
CREATE INDEX idx_documento_tipo_numero ON cliente_documento(tipo, numero);

-- ============================================================================
-- CLIENTE_ENDERECO (Enderecos do cliente)
-- ============================================================================

CREATE TABLE cliente_endereco (
    id UUID PRIMARY KEY,
    cliente_id UUID NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    cep VARCHAR(10) NOT NULL,
    logradouro VARCHAR(255) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    complemento VARCHAR(100),
    bairro VARCHAR(100) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    estado VARCHAR(2) NOT NULL,
    pais VARCHAR(50) NOT NULL DEFAULT 'Brasil',
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_endereco_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE,
    CONSTRAINT ck_endereco_tipo CHECK (tipo IN ('RESIDENCIAL', 'COMERCIAL', 'OUTRO'))
);

CREATE INDEX idx_endereco_cliente ON cliente_endereco(cliente_id);
CREATE INDEX idx_endereco_cep ON cliente_endereco(cep);

-- ============================================================================
-- CLIENTE_CONTATO (Contatos do cliente)
-- ============================================================================

CREATE TABLE cliente_contato (
    id UUID PRIMARY KEY,
    cliente_id UUID NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    valor VARCHAR(255) NOT NULL,
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_contato_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE,
    CONSTRAINT ck_contato_tipo CHECK (tipo IN ('EMAIL', 'CELULAR', 'TELEFONE', 'WHATSAPP', 'OUTRO'))
);

CREATE INDEX idx_contato_cliente ON cliente_contato(cliente_id);
CREATE INDEX idx_contato_tipo ON cliente_contato(tipo);

-- ============================================================================
-- CLIENTE_VIAGEM (Viagens do cliente)
-- ============================================================================

CREATE TABLE cliente_viagem (
    id UUID PRIMARY KEY,
    cliente_id UUID NOT NULL,
    localizador VARCHAR(20) NOT NULL,
    aeroportos CLOB NOT NULL,  -- JSON array de codigos IATA
    data_embarque TIMESTAMP NOT NULL,
    assento VARCHAR(10),
    companhias CLOB NOT NULL,  -- JSON array de companhias
    moeda VARCHAR(3) NOT NULL DEFAULT 'BRL',
    valor DECIMAL(15, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'RESERVADO',
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_viagem_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE,
    CONSTRAINT ck_viagem_status CHECK (status IN ('RESERVADO', 'EMITIDO', 'CANCELADO', 'CONCLUIDO'))
);

CREATE INDEX idx_cliente_viagem_cliente ON cliente_viagem(cliente_id);
CREATE INDEX idx_cliente_viagem_localizador ON cliente_viagem(localizador);
CREATE INDEX idx_cliente_viagem_data ON cliente_viagem(data_embarque);

-- ============================================================================
-- SEED DATA: Clientes de Teste
-- ============================================================================

-- Tenant padrao
-- Nota: account com id '00000000-0000-0000-0000-000000000001' ja existe em V001

-- ============================================================================
-- TITULAR 1: Joao Silva (Ativo, com familia)
-- ============================================================================

INSERT INTO cliente (id, tenant_id, tipo, nome, nome_do_meio, sobrenome, data_nascimento, sexo, ativo, observacoes)
VALUES (
    '550e8400-e29b-41d4-a716-446655440001',
    '00000000-0000-0000-0000-000000000001',
    'TITULAR',
    'Joao',
    'Carlos',
    'Silva',
    '1985-03-20',
    'MASCULINO',
    TRUE,
    'Cliente VIP, viaja frequentemente para Europa'
);

-- Documentos Joao
INSERT INTO cliente_documento (id, cliente_id, tipo, numero, data_emissao, principal) VALUES
    ('d0000000-0000-0000-0000-000000000001', '550e8400-e29b-41d4-a716-446655440001', 'CPF', '123.456.789-00', '2020-01-01', TRUE),
    ('d0000000-0000-0000-0000-000000000002', '550e8400-e29b-41d4-a716-446655440001', 'PASSAPORTE', 'BR123456', '2020-01-01', FALSE);

-- Enderecos Joao
INSERT INTO cliente_endereco (id, cliente_id, tipo, cep, logradouro, numero, complemento, bairro, cidade, estado, principal) VALUES
    ('e0000000-0000-0000-0000-000000000001', '550e8400-e29b-41d4-a716-446655440001', 'RESIDENCIAL', '01310-100', 'Av. Paulista', '1000', 'Apto 1501', 'Bela Vista', 'Sao Paulo', 'SP', TRUE);

-- Contatos Joao
INSERT INTO cliente_contato (id, cliente_id, tipo, valor, principal) VALUES
    ('c0000000-0000-0000-0000-000000000001', '550e8400-e29b-41d4-a716-446655440001', 'EMAIL', 'joao@example.com', TRUE),
    ('c0000000-0000-0000-0000-000000000002', '550e8400-e29b-41d4-a716-446655440001', 'CELULAR', '+55 11 99999-8888', FALSE);

-- Viagem Joao (Europa)
INSERT INTO cliente_viagem (id, cliente_id, localizador, aeroportos, data_embarque, assento, companhias, moeda, valor, status) VALUES
    ('a0000000-0000-0000-0000-000000000001', '550e8400-e29b-41d4-a716-446655440001', 'EUR123', '["GRU", "LIS", "CDG"]', '2024-09-15 08:00:00', '3A', '["TAP", "AF"]', 'EUR', 3500.00, 'EMITIDO');

-- ============================================================================
-- DEPENDENTES DO JOAO
-- ============================================================================

-- Ana (Conjuge)
INSERT INTO cliente (id, tenant_id, tipo, nome, sobrenome, data_nascimento, sexo, ativo)
VALUES (
    '550e8400-e29b-41d4-a716-446655440010',
    '00000000-0000-0000-0000-000000000001',
    'DEPENDENTE',
    'Ana',
    'Silva',
    '1987-05-15',
    'FEMININO',
    TRUE
);

INSERT INTO cliente_dependente_vinculo (cliente_id, titular_id, parentesco)
VALUES ('550e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440001', 'CONJUGE');

INSERT INTO cliente_documento (id, cliente_id, tipo, numero, data_emissao, principal) VALUES
    ('d0000000-0000-0000-0000-000000000010', '550e8400-e29b-41d4-a716-446655440010', 'CPF', '222.333.444-55', '2020-01-01', TRUE);

INSERT INTO cliente_contato (id, cliente_id, tipo, valor, principal) VALUES
    ('c0000000-0000-0000-0000-000000000010', '550e8400-e29b-41d4-a716-446655440010', 'EMAIL', 'ana@example.com', TRUE);

-- Pedro (Filho)
INSERT INTO cliente (id, tenant_id, tipo, nome, sobrenome, data_nascimento, sexo, ativo)
VALUES (
    '550e8400-e29b-41d4-a716-446655440011',
    '00000000-0000-0000-0000-000000000001',
    'DEPENDENTE',
    'Pedro',
    'Silva',
    '2015-08-10',
    'MASCULINO',
    TRUE
);

INSERT INTO cliente_dependente_vinculo (cliente_id, titular_id, parentesco)
VALUES ('550e8400-e29b-41d4-a716-446655440011', '550e8400-e29b-41d4-a716-446655440001', 'FILHO');

INSERT INTO cliente_documento (id, cliente_id, tipo, numero, data_emissao, principal) VALUES
    ('d0000000-0000-0000-0000-000000000011', '550e8400-e29b-41d4-a716-446655440011', 'RG', '12.345.678-9', '2020-01-01', TRUE);

-- Julia (Filha)
INSERT INTO cliente (id, tenant_id, tipo, nome, sobrenome, data_nascimento, sexo, ativo)
VALUES (
    '550e8400-e29b-41d4-a716-446655440012',
    '00000000-0000-0000-0000-000000000001',
    'DEPENDENTE',
    'Julia',
    'Silva',
    '2018-03-22',
    'FEMININO',
    TRUE
);

INSERT INTO cliente_dependente_vinculo (cliente_id, titular_id, parentesco)
VALUES ('550e8400-e29b-41d4-a716-446655440012', '550e8400-e29b-41d4-a716-446655440001', 'FILHA');

INSERT INTO cliente_documento (id, cliente_id, tipo, numero, data_emissao, principal) VALUES
    ('d0000000-0000-0000-0000-000000000012', '550e8400-e29b-41d4-a716-446655440012', 'RG', '98.765.432-1', '2020-01-01', TRUE);

-- ============================================================================
-- TITULAR 2: Maria Santos (Ativa, sem dependentes)
-- ============================================================================

INSERT INTO cliente (id, tenant_id, tipo, nome, sobrenome, data_nascimento, sexo, ativo, observacoes)
VALUES (
    '550e8400-e29b-41d4-a716-446655440002',
    '00000000-0000-0000-0000-000000000001',
    'TITULAR',
    'Maria',
    'Santos',
    '1990-06-15',
    'FEMININO',
    TRUE,
    'Prefere voos diretos'
);

INSERT INTO cliente_documento (id, cliente_id, tipo, numero, data_emissao, principal) VALUES
    ('d0000000-0000-0000-0000-000000000020', '550e8400-e29b-41d4-a716-446655440002', 'CPF', '987.654.321-00', '2020-01-01', TRUE);

INSERT INTO cliente_endereco (id, cliente_id, tipo, cep, logradouro, numero, bairro, cidade, estado, principal) VALUES
    ('e0000000-0000-0000-0000-000000000020', '550e8400-e29b-41d4-a716-446655440002', 'RESIDENCIAL', '22070-000', 'Av. Atlantica', '500', 'Copacabana', 'Rio de Janeiro', 'RJ', TRUE);

INSERT INTO cliente_contato (id, cliente_id, tipo, valor, principal) VALUES
    ('c0000000-0000-0000-0000-000000000020', '550e8400-e29b-41d4-a716-446655440002', 'EMAIL', 'maria@example.com', TRUE),
    ('c0000000-0000-0000-0000-000000000021', '550e8400-e29b-41d4-a716-446655440002', 'CELULAR', '+55 21 99999-8888', FALSE);

INSERT INTO cliente_viagem (id, cliente_id, localizador, aeroportos, data_embarque, assento, companhias, moeda, valor, status) VALUES
    ('a0000000-0000-0000-0000-000000000020', '550e8400-e29b-41d4-a716-446655440002', 'USA456', '["GRU", "MIA"]', '2024-07-20 14:00:00', '15B', '["AA"]', 'USD', 2000.00, 'EMITIDO');

-- ============================================================================
-- TITULAR 3: Carlos Pereira (Inativo)
-- ============================================================================

INSERT INTO cliente (id, tenant_id, tipo, nome, nome_do_meio, sobrenome, data_nascimento, sexo, ativo, observacoes)
VALUES (
    '550e8400-e29b-41d4-a716-446655440003',
    '00000000-0000-0000-0000-000000000001',
    'TITULAR',
    'Carlos',
    'Eduardo',
    'Pereira',
    '1980-12-05',
    'MASCULINO',
    FALSE,
    'Cliente inativo desde 2023'
);

INSERT INTO cliente_documento (id, cliente_id, tipo, numero, data_emissao, data_validade, principal) VALUES
    ('d0000000-0000-0000-0000-000000000030', '550e8400-e29b-41d4-a716-446655440003', 'CPF', '111.222.333-44', '2020-01-01', NULL, TRUE),
    ('d0000000-0000-0000-0000-000000000031', '550e8400-e29b-41d4-a716-446655440003', 'PASSAPORTE', 'BR654321', '2020-01-01', '2030-01-01', FALSE);

INSERT INTO cliente_endereco (id, cliente_id, tipo, cep, logradouro, numero, bairro, cidade, estado, principal) VALUES
    ('e0000000-0000-0000-0000-000000000030', '550e8400-e29b-41d4-a716-446655440003', 'RESIDENCIAL', '13015-000', 'Rua Barao de Jaguara', '200', 'Centro', 'Campinas', 'SP', TRUE);

INSERT INTO cliente_contato (id, cliente_id, tipo, valor, principal) VALUES
    ('c0000000-0000-0000-0000-000000000030', '550e8400-e29b-41d4-a716-446655440003', 'EMAIL', 'carlos@example.com', TRUE);

-- ============================================================================
-- SUMMARY: Test Data
-- ============================================================================
-- | Cliente          | Tipo       | Ativo | Dependentes | Viagens |
-- |------------------|------------|-------|-------------|---------|
-- | Joao Silva       | TITULAR    | Sim   | 3 (familia) | 1       |
-- | Ana Silva        | DEPENDENTE | Sim   | -           | 0       |
-- | Pedro Silva      | DEPENDENTE | Sim   | -           | 0       |
-- | Julia Silva      | DEPENDENTE | Sim   | -           | 0       |
-- | Maria Santos     | TITULAR    | Sim   | 0           | 1       |
-- | Carlos Pereira   | TITULAR    | Nao   | 0           | 0       |
-- ============================================================================

-- ============================================================================
-- RECREATE CONTA_PROGRAMA (Miles account) - dropped earlier
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
    CONSTRAINT fk_conta_programa FOREIGN KEY (programa_id) REFERENCES programa_milhas(id),
    CONSTRAINT uk_conta_owner UNIQUE (tenant_id, programa_id, owner)
);

CREATE INDEX idx_conta_tenant ON conta_programa(tenant_id);
CREATE INDEX idx_conta_owner ON conta_programa(owner);
CREATE INDEX idx_conta_approval ON conta_programa(tenant_id, approval_status);

-- ============================================================================
-- RECREATE TRANSACAO - dropped earlier (depends on conta_programa)
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
-- RECREATE VIAGEM - dropped earlier
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
    CONSTRAINT fk_viagem_created_by FOREIGN KEY (created_by) REFERENCES "user"(id),
    CONSTRAINT fk_viagem_approved_by FOREIGN KEY (approved_by) REFERENCES "user"(id)
);

CREATE INDEX idx_viagem_account ON viagem(account_id);
CREATE INDEX idx_viagem_data ON viagem(data_ida);

-- ============================================================================
-- SEED DATA: Sample Conta Programa (miles accounts) - for tenant 1
-- ============================================================================

INSERT INTO conta_programa (id, tenant_id, cliente_id, programa_id, programa_nome, owner, saldo_milhas, custo_base_total_brl, custo_medio_milheiro_atual, status, approval_status, created_by, approved_by, approved_at)
VALUES (
    '60000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000001',
    '550e8400-e29b-41d4-a716-446655440001',
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
    '550e8400-e29b-41d4-a716-446655440002',
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
-- SEED DATA: Sample Transacoes
-- ============================================================================

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

INSERT INTO transacao (id, conta_programa_id, tipo, milhas, valor_brl, fonte, observacao, data, approval_status, created_by, approved_by, approved_at, criado_em)
VALUES (
    '80000000-0000-0000-0000-000000000003',
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
