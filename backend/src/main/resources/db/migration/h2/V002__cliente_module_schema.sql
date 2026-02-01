-- ============================================================================
-- V002: Cliente Module Schema for H2 (Development)
-- ============================================================================

-- ============================================================================
-- CLIENTE_MODULE (New simplified cliente for the module)
-- This is separate from the existing 'cliente' table to avoid conflicts
-- ============================================================================

CREATE TABLE IF NOT EXISTS cliente_module (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    nome VARCHAR(100) NOT NULL,
    sobrenome VARCHAR(100) NOT NULL,
    data_nascimento DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    observacoes VARCHAR(1000),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cliente_module_tenant FOREIGN KEY (tenant_id) REFERENCES account(id) ON DELETE CASCADE
);

CREATE INDEX idx_cliente_module_tenant ON cliente_module(tenant_id);
CREATE INDEX idx_cliente_module_nome ON cliente_module(nome, sobrenome);
CREATE INDEX idx_cliente_module_status ON cliente_module(tenant_id, status);

-- ============================================================================
-- CLIENTE_DOCUMENTO (Customer documents)
-- ============================================================================

CREATE TABLE IF NOT EXISTS cliente_documento (
    id UUID PRIMARY KEY,
    cliente_id UUID NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    numero VARCHAR(50) NOT NULL,
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    arquivo_url VARCHAR(500),
    nome_arquivo VARCHAR(255),
    CONSTRAINT fk_cliente_documento_cliente FOREIGN KEY (cliente_id) REFERENCES cliente_module(id) ON DELETE CASCADE
);

CREATE INDEX idx_cliente_documento_cliente ON cliente_documento(cliente_id);
CREATE INDEX idx_cliente_documento_tipo ON cliente_documento(tipo, numero);

-- ============================================================================
-- CLIENTE_ENDERECO (Customer addresses)
-- ============================================================================

CREATE TABLE IF NOT EXISTS cliente_endereco (
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
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_cliente_endereco_cliente FOREIGN KEY (cliente_id) REFERENCES cliente_module(id) ON DELETE CASCADE
);

CREATE INDEX idx_cliente_endereco_cliente ON cliente_endereco(cliente_id);
CREATE INDEX idx_cliente_endereco_cep ON cliente_endereco(cep);

-- ============================================================================
-- CLIENTE_CONTATO (Customer contacts)
-- ============================================================================

CREATE TABLE IF NOT EXISTS cliente_contato (
    id UUID PRIMARY KEY,
    cliente_id UUID NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    valor VARCHAR(255) NOT NULL,
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_cliente_contato_cliente FOREIGN KEY (cliente_id) REFERENCES cliente_module(id) ON DELETE CASCADE
);

CREATE INDEX idx_cliente_contato_cliente ON cliente_contato(cliente_id);
CREATE INDEX idx_cliente_contato_tipo ON cliente_contato(tipo);

-- ============================================================================
-- SEED DATA: Sample Clients for Cliente Module
-- ============================================================================

-- Cliente 1: Maria Silva (Ativo)
INSERT INTO cliente_module (id, tenant_id, nome, sobrenome, data_nascimento, status, observacoes)
VALUES (
    'c1000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000001',
    'Maria',
    'Silva',
    '1985-03-15',
    'ATIVO',
    'Cliente VIP, sempre viaja para Europa'
);

-- Documentos Maria
INSERT INTO cliente_documento (id, cliente_id, tipo, numero, principal, arquivo_url, nome_arquivo) VALUES
    ('d1000000-0000-0000-0000-000000000001', 'c1000000-0000-0000-0000-000000000001', 'CPF', '123.456.789-00', TRUE, NULL, NULL),
    ('d1000000-0000-0000-0000-000000000002', 'c1000000-0000-0000-0000-000000000001', 'PASSAPORTE', 'BR123456', FALSE, '/docs/passaporte_maria.pdf', 'passaporte_maria.pdf');

-- Enderecos Maria
INSERT INTO cliente_endereco (id, cliente_id, tipo, cep, logradouro, numero, complemento, bairro, cidade, estado, principal) VALUES
    ('e1000000-0000-0000-0000-000000000001', 'c1000000-0000-0000-0000-000000000001', 'RESIDENCIAL', '01310-100', 'Av. Paulista', '1000', 'Apto 1501', 'Bela Vista', 'Sao Paulo', 'SP', TRUE),
    ('e1000000-0000-0000-0000-000000000002', 'c1000000-0000-0000-0000-000000000001', 'COMERCIAL', '04538-132', 'Av. Faria Lima', '3500', 'Sala 301', 'Itaim Bibi', 'Sao Paulo', 'SP', FALSE);

-- Contatos Maria
INSERT INTO cliente_contato (id, cliente_id, tipo, valor, principal) VALUES
    ('ca100000-0000-0000-0000-000000000001', 'c1000000-0000-0000-0000-000000000001', 'EMAIL', 'maria.silva@email.com', TRUE),
    ('ca100000-0000-0000-0000-000000000002', 'c1000000-0000-0000-0000-000000000001', 'CELULAR', '11988887777', FALSE),
    ('ca100000-0000-0000-0000-000000000003', 'c1000000-0000-0000-0000-000000000001', 'WHATSAPP', '11988887777', FALSE);

-- Cliente 2: Joao Santos (Ativo)
INSERT INTO cliente_module (id, tenant_id, nome, sobrenome, data_nascimento, status, observacoes)
VALUES (
    'c1000000-0000-0000-0000-000000000002',
    '00000000-0000-0000-0000-000000000001',
    'Joao',
    'Santos',
    '1978-07-22',
    'ATIVO',
    'Viajante frequente, prefere milhas Smiles'
);

-- Documentos Joao
INSERT INTO cliente_documento (id, cliente_id, tipo, numero, principal, arquivo_url, nome_arquivo) VALUES
    ('d1000000-0000-0000-0000-000000000003', 'c1000000-0000-0000-0000-000000000002', 'CPF', '987.654.321-00', TRUE, NULL, NULL),
    ('d1000000-0000-0000-0000-000000000004', 'c1000000-0000-0000-0000-000000000002', 'RG', '12.345.678-9', FALSE, NULL, NULL);

-- Enderecos Joao
INSERT INTO cliente_endereco (id, cliente_id, tipo, cep, logradouro, numero, complemento, bairro, cidade, estado, principal) VALUES
    ('e1000000-0000-0000-0000-000000000003', 'c1000000-0000-0000-0000-000000000002', 'RESIDENCIAL', '22041-080', 'Rua Barata Ribeiro', '200', 'Apto 302', 'Copacabana', 'Rio de Janeiro', 'RJ', TRUE);

-- Contatos Joao
INSERT INTO cliente_contato (id, cliente_id, tipo, valor, principal) VALUES
    ('ca100000-0000-0000-0000-000000000004', 'c1000000-0000-0000-0000-000000000002', 'EMAIL', 'joao.santos@empresa.com.br', TRUE),
    ('ca100000-0000-0000-0000-000000000005', 'c1000000-0000-0000-0000-000000000002', 'TELEFONE', '2133334444', FALSE);

-- Cliente 3: Ana Oliveira (Pendente)
INSERT INTO cliente_module (id, tenant_id, nome, sobrenome, data_nascimento, status, observacoes)
VALUES (
    'c1000000-0000-0000-0000-000000000003',
    '00000000-0000-0000-0000-000000000001',
    'Ana',
    'Oliveira',
    '1990-11-08',
    'PENDENTE',
    'Aguardando confirmacao de documentos'
);

-- Documentos Ana
INSERT INTO cliente_documento (id, cliente_id, tipo, numero, principal, arquivo_url, nome_arquivo) VALUES
    ('d1000000-0000-0000-0000-000000000005', 'c1000000-0000-0000-0000-000000000003', 'CPF', '111.222.333-44', TRUE, NULL, NULL);

-- Enderecos Ana
INSERT INTO cliente_endereco (id, cliente_id, tipo, cep, logradouro, numero, complemento, bairro, cidade, estado, principal) VALUES
    ('e1000000-0000-0000-0000-000000000004', 'c1000000-0000-0000-0000-000000000003', 'RESIDENCIAL', '30130-000', 'Av. Afonso Pena', '1500', NULL, 'Centro', 'Belo Horizonte', 'MG', TRUE);

-- Contatos Ana
INSERT INTO cliente_contato (id, cliente_id, tipo, valor, principal) VALUES
    ('ca100000-0000-0000-0000-000000000006', 'c1000000-0000-0000-0000-000000000003', 'EMAIL', 'ana.oliveira@gmail.com', TRUE);

-- Cliente 4: Carlos Ferreira (Inativo)
INSERT INTO cliente_module (id, tenant_id, nome, sobrenome, data_nascimento, status, observacoes)
VALUES (
    'c1000000-0000-0000-0000-000000000004',
    '00000000-0000-0000-0000-000000000001',
    'Carlos',
    'Ferreira',
    '1965-01-30',
    'INATIVO',
    'Cliente inativo desde 2023'
);

-- Documentos Carlos
INSERT INTO cliente_documento (id, cliente_id, tipo, numero, principal, arquivo_url, nome_arquivo) VALUES
    ('d1000000-0000-0000-0000-000000000006', 'c1000000-0000-0000-0000-000000000004', 'CPF', '555.666.777-88', TRUE, NULL, NULL);

-- Enderecos Carlos
INSERT INTO cliente_endereco (id, cliente_id, tipo, cep, logradouro, numero, complemento, bairro, cidade, estado, principal) VALUES
    ('e1000000-0000-0000-0000-000000000005', 'c1000000-0000-0000-0000-000000000004', 'RESIDENCIAL', '80010-000', 'Rua XV de Novembro', '100', 'Casa', 'Centro', 'Curitiba', 'PR', TRUE);

-- Contatos Carlos
INSERT INTO cliente_contato (id, cliente_id, tipo, valor, principal) VALUES
    ('ca100000-0000-0000-0000-000000000007', 'c1000000-0000-0000-0000-000000000004', 'EMAIL', 'carlos.ferreira@outlook.com', TRUE);

-- ============================================================================
-- SUMMARY: Test Clients
-- ============================================================================
-- | Nome            | Status   | Documentos | Enderecos | Contatos |
-- |-----------------|----------|------------|-----------|----------|
-- | Maria Silva     | ATIVO    | 2          | 2         | 3        |
-- | Joao Santos     | ATIVO    | 2          | 1         | 2        |
-- | Ana Oliveira    | PENDENTE | 1          | 1         | 1        |
-- | Carlos Ferreira | INATIVO  | 1          | 1         | 1        |
-- ============================================================================
