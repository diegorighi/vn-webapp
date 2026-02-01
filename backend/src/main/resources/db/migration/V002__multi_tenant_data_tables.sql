-- ============================================================================
-- V002: Multi-Tenant Data Tables
-- ============================================================================
-- This migration adds multi-tenant support to all business data tables:
-- 1. Adds account_id FK to all tables
-- 2. Adds approval workflow columns (status, created_by, approved_by)
-- 3. Creates RLS policies for data isolation
-- 4. Creates views for approved-only data
-- ============================================================================

-- ============================================================================
-- PROGRAMA DE MILHAS (Reference data - shared or per-account)
-- ============================================================================

CREATE TABLE programa_milhas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID REFERENCES account(id) ON DELETE CASCADE,  -- NULL = global/shared

    -- Program details
    brand VARCHAR(100) NOT NULL,
    nome_completo VARCHAR(255),
    logo_url VARCHAR(500),
    website_url VARCHAR(500),

    -- Configuration
    moeda VARCHAR(3) NOT NULL DEFAULT 'BRL',
    tipo VARCHAR(50) NOT NULL DEFAULT 'AIRLINE',  -- AIRLINE, HOTEL, BANK, OTHER
    pais VARCHAR(2) NOT NULL DEFAULT 'BR',

    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Constraints
    CONSTRAINT programa_brand_unique_per_account UNIQUE (account_id, brand)
);

CREATE INDEX idx_programa_account ON programa_milhas(account_id);
CREATE INDEX idx_programa_brand ON programa_milhas(brand);
CREATE INDEX idx_programa_status ON programa_milhas(status);

-- ============================================================================
-- CLIENTE (Customer within an account)
-- ============================================================================

CREATE TABLE cliente (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID NOT NULL REFERENCES account(id) ON DELETE CASCADE,

    -- Titular/Dependente relationship
    titular_id UUID,                              -- NULL = titular, NOT NULL = dependente
    parentesco VARCHAR(20),                       -- CONJUGE, FILHO, FILHA, PAI, MAE, OUTRO

    -- Identification
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    cpf VARCHAR(14),
    rg VARCHAR(20),

    -- Contact
    telefone VARCHAR(20),
    celular VARCHAR(20),

    -- Address
    endereco_logradouro VARCHAR(255),
    endereco_numero VARCHAR(20),
    endereco_complemento VARCHAR(100),
    endereco_bairro VARCHAR(100),
    endereco_cidade VARCHAR(100),
    endereco_estado VARCHAR(2),
    endereco_cep VARCHAR(10),

    -- Personal
    data_nascimento DATE,
    sexo VARCHAR(1),

    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    -- Approval workflow
    approval_status approval_status NOT NULL DEFAULT 'PENDING',
    created_by UUID NOT NULL REFERENCES "user"(id),
    approved_by UUID REFERENCES "user"(id),
    approved_at TIMESTAMP,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Constraints
    CONSTRAINT cliente_cpf_unique_per_account UNIQUE (account_id, cpf),
    CONSTRAINT cliente_email_unique_per_account UNIQUE (account_id, email)
);

CREATE INDEX idx_cliente_account ON cliente(account_id);
CREATE INDEX idx_cliente_nome ON cliente(nome);
CREATE INDEX idx_cliente_cpf ON cliente(cpf);
CREATE INDEX idx_cliente_titular ON cliente(titular_id);
CREATE INDEX idx_cliente_approval ON cliente(account_id, approval_status);
CREATE INDEX idx_cliente_created_by ON cliente(created_by);

-- FK for titular/dependente (self-referencing, added after table creation)
ALTER TABLE cliente ADD CONSTRAINT fk_cliente_titular
    FOREIGN KEY (titular_id) REFERENCES cliente(id) ON DELETE CASCADE;

-- ============================================================================
-- CONTA PROGRAMA (Miles account for a customer in a program)
-- ============================================================================

CREATE TABLE conta_programa (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID NOT NULL REFERENCES account(id) ON DELETE CASCADE,

    -- Relationships
    cliente_id UUID REFERENCES cliente(id) ON DELETE SET NULL,
    programa_id UUID NOT NULL REFERENCES programa_milhas(id),

    -- Denormalized for queries
    programa_nome VARCHAR(100) NOT NULL,
    owner VARCHAR(255) NOT NULL,  -- Owner name (can be different from cliente)

    -- Balance tracking
    saldo_milhas BIGINT NOT NULL DEFAULT 0,
    custo_base_total_brl DECIMAL(15, 2) NOT NULL DEFAULT 0,
    custo_medio_milheiro_atual DECIMAL(15, 6) NOT NULL DEFAULT 0,

    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    -- Approval workflow
    approval_status approval_status NOT NULL DEFAULT 'PENDING',
    created_by UUID NOT NULL REFERENCES "user"(id),
    approved_by UUID REFERENCES "user"(id),
    approved_at TIMESTAMP,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Constraints
    CONSTRAINT conta_programa_saldo_positive CHECK (saldo_milhas >= 0),
    CONSTRAINT conta_programa_custo_positive CHECK (custo_base_total_brl >= 0),
    CONSTRAINT conta_unique_per_owner UNIQUE (account_id, programa_id, owner)
);

CREATE INDEX idx_conta_programa_account ON conta_programa(account_id);
CREATE INDEX idx_conta_programa_cliente ON conta_programa(cliente_id);
CREATE INDEX idx_conta_programa_programa ON conta_programa(programa_id);
CREATE INDEX idx_conta_programa_owner ON conta_programa(owner);
CREATE INDEX idx_conta_programa_approval ON conta_programa(account_id, approval_status);

-- ============================================================================
-- TRANSACAO (Immutable transaction records)
-- ============================================================================

CREATE TABLE transacao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID NOT NULL REFERENCES account(id) ON DELETE CASCADE,

    -- Relationships
    conta_programa_id UUID NOT NULL REFERENCES conta_programa(id),

    -- Transaction details
    tipo VARCHAR(20) NOT NULL,  -- COMPRA, VENDA, BONUS
    milhas BIGINT NOT NULL,
    valor_brl DECIMAL(15, 2) NOT NULL DEFAULT 0,
    fonte VARCHAR(100),  -- Source of bonus/cashback
    observacao TEXT,

    -- Transaction date
    data TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Approval workflow
    approval_status approval_status NOT NULL DEFAULT 'PENDING',
    created_by UUID NOT NULL REFERENCES "user"(id),
    approved_by UUID REFERENCES "user"(id),
    approved_at TIMESTAMP,

    -- Timestamps (criado_em is immutable)
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Constraints
    CONSTRAINT transacao_milhas_positive CHECK (milhas > 0),
    CONSTRAINT transacao_tipo_valid CHECK (tipo IN ('COMPRA', 'VENDA', 'BONUS'))
);

CREATE INDEX idx_transacao_account ON transacao(account_id);
CREATE INDEX idx_transacao_conta ON transacao(conta_programa_id);
CREATE INDEX idx_transacao_tipo ON transacao(tipo);
CREATE INDEX idx_transacao_data ON transacao(data DESC);
CREATE INDEX idx_transacao_approval ON transacao(account_id, approval_status);
CREATE INDEX idx_transacao_created_by ON transacao(created_by);

-- ============================================================================
-- CREDENCIAL PROGRAMA (Encrypted credentials for external integrations)
-- ============================================================================

CREATE TABLE credencial_programa (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID NOT NULL REFERENCES account(id) ON DELETE CASCADE,

    -- Relationships
    cliente_id UUID REFERENCES cliente(id) ON DELETE SET NULL,
    programa_id UUID NOT NULL REFERENCES programa_milhas(id),

    -- Encrypted credentials (encrypted with account's DEK)
    usuario_criptografado BYTEA,
    senha_criptografada BYTEA,
    access_token BYTEA,
    refresh_token BYTEA,
    token_expira TIMESTAMP,

    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    ultima_consulta TIMESTAMP,
    ultima_sincronizacao TIMESTAMP,

    -- Approval workflow
    approval_status approval_status NOT NULL DEFAULT 'PENDING',
    created_by UUID NOT NULL REFERENCES "user"(id),
    approved_by UUID REFERENCES "user"(id),
    approved_at TIMESTAMP,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_credencial_account ON credencial_programa(account_id);
CREATE INDEX idx_credencial_cliente ON credencial_programa(cliente_id);
CREATE INDEX idx_credencial_programa ON credencial_programa(programa_id);
CREATE INDEX idx_credencial_approval ON credencial_programa(account_id, approval_status);

-- ============================================================================
-- SALDO MILHAS EXTERNO (External balance snapshots from integrations)
-- ============================================================================

CREATE TABLE saldo_milhas_externo (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID NOT NULL REFERENCES account(id) ON DELETE CASCADE,

    -- Relationships
    credencial_id UUID NOT NULL REFERENCES credencial_programa(id) ON DELETE CASCADE,

    -- Balance data
    saldo_milhas BIGINT NOT NULL,
    saldo_nivel VARCHAR(50),  -- Loyalty tier (Gold, Platinum, etc.)
    milhas_expirar BIGINT,
    data_expiracao DATE,

    -- Source
    fonte_consulta VARCHAR(50) NOT NULL,  -- API, WEB_SCRAPING, MANUAL
    consulta_timestamp TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Raw response (for debugging)
    raw_response JSONB,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_saldo_externo_account ON saldo_milhas_externo(account_id);
CREATE INDEX idx_saldo_externo_credencial ON saldo_milhas_externo(credencial_id);
CREATE INDEX idx_saldo_externo_timestamp ON saldo_milhas_externo(consulta_timestamp DESC);

-- ============================================================================
-- VIAGEM (Travel records)
-- ============================================================================

CREATE TABLE viagem (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID NOT NULL REFERENCES account(id) ON DELETE CASCADE,

    -- Relationships
    cliente_id UUID REFERENCES cliente(id) ON DELETE SET NULL,

    -- Trip details
    destino VARCHAR(255) NOT NULL,
    origem VARCHAR(255),
    data_ida DATE NOT NULL,
    data_volta DATE,

    -- Costs
    valor_total DECIMAL(15, 2),
    milhas_utilizadas BIGINT,

    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNED',  -- PLANNED, CONFIRMED, COMPLETED, CANCELLED

    -- Notes
    observacoes TEXT,

    -- Approval workflow
    approval_status approval_status NOT NULL DEFAULT 'PENDING',
    created_by UUID NOT NULL REFERENCES "user"(id),
    approved_by UUID REFERENCES "user"(id),
    approved_at TIMESTAMP,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_viagem_account ON viagem(account_id);
CREATE INDEX idx_viagem_cliente ON viagem(cliente_id);
CREATE INDEX idx_viagem_data ON viagem(data_ida);
CREATE INDEX idx_viagem_approval ON viagem(account_id, approval_status);

-- ============================================================================
-- ENABLE ROW-LEVEL SECURITY
-- ============================================================================

ALTER TABLE programa_milhas ENABLE ROW LEVEL SECURITY;
ALTER TABLE cliente ENABLE ROW LEVEL SECURITY;
ALTER TABLE conta_programa ENABLE ROW LEVEL SECURITY;
ALTER TABLE transacao ENABLE ROW LEVEL SECURITY;
ALTER TABLE credencial_programa ENABLE ROW LEVEL SECURITY;
ALTER TABLE saldo_milhas_externo ENABLE ROW LEVEL SECURITY;
ALTER TABLE viagem ENABLE ROW LEVEL SECURITY;

-- ============================================================================
-- RLS POLICIES FOR DATA TABLES
-- ============================================================================

-- Programa Milhas (visible if global OR belongs to account)
CREATE POLICY programa_isolation ON programa_milhas
    FOR ALL
    USING (
        account_id IS NULL OR
        account_id = current_setting('app.current_account_id', true)::uuid
    )
    WITH CHECK (
        account_id IS NULL OR
        account_id = current_setting('app.current_account_id', true)::uuid
    );

-- Cliente
CREATE POLICY cliente_isolation ON cliente
    FOR ALL
    USING (account_id = current_setting('app.current_account_id', true)::uuid)
    WITH CHECK (account_id = current_setting('app.current_account_id', true)::uuid);

-- Conta Programa
CREATE POLICY conta_programa_isolation ON conta_programa
    FOR ALL
    USING (account_id = current_setting('app.current_account_id', true)::uuid)
    WITH CHECK (account_id = current_setting('app.current_account_id', true)::uuid);

-- Transacao
CREATE POLICY transacao_isolation ON transacao
    FOR ALL
    USING (account_id = current_setting('app.current_account_id', true)::uuid)
    WITH CHECK (account_id = current_setting('app.current_account_id', true)::uuid);

-- Credencial Programa
CREATE POLICY credencial_isolation ON credencial_programa
    FOR ALL
    USING (account_id = current_setting('app.current_account_id', true)::uuid)
    WITH CHECK (account_id = current_setting('app.current_account_id', true)::uuid);

-- Saldo Milhas Externo
CREATE POLICY saldo_externo_isolation ON saldo_milhas_externo
    FOR ALL
    USING (account_id = current_setting('app.current_account_id', true)::uuid)
    WITH CHECK (account_id = current_setting('app.current_account_id', true)::uuid);

-- Viagem
CREATE POLICY viagem_isolation ON viagem
    FOR ALL
    USING (account_id = current_setting('app.current_account_id', true)::uuid)
    WITH CHECK (account_id = current_setting('app.current_account_id', true)::uuid);

-- ============================================================================
-- VIEWS FOR APPROVED-ONLY DATA
-- ============================================================================

-- These views only show data that has been approved (visible to all users)
-- Pending data is only visible to the creator and approvers

CREATE VIEW v_cliente_approved AS
SELECT * FROM cliente
WHERE approval_status = 'APPROVED' OR approval_status = 'AUTO_APPROVED';

CREATE VIEW v_conta_programa_approved AS
SELECT * FROM conta_programa
WHERE approval_status = 'APPROVED' OR approval_status = 'AUTO_APPROVED';

CREATE VIEW v_transacao_approved AS
SELECT * FROM transacao
WHERE approval_status = 'APPROVED' OR approval_status = 'AUTO_APPROVED';

CREATE VIEW v_credencial_approved AS
SELECT * FROM credencial_programa
WHERE approval_status = 'APPROVED' OR approval_status = 'AUTO_APPROVED';

CREATE VIEW v_viagem_approved AS
SELECT * FROM viagem
WHERE approval_status = 'APPROVED' OR approval_status = 'AUTO_APPROVED';

-- ============================================================================
-- FUNCTION: Auto-approve for admins
-- ============================================================================

CREATE OR REPLACE FUNCTION check_auto_approve()
RETURNS TRIGGER AS $$
DECLARE
    user_can_approve BOOLEAN;
BEGIN
    -- Check if the creating user has approval rights
    SELECT EXISTS (
        SELECT 1
        FROM user_role ur
        JOIN role r ON r.id = ur.role_id
        WHERE ur.user_id = NEW.created_by
        AND r.can_approve = TRUE
    ) INTO user_can_approve;

    IF user_can_approve THEN
        NEW.approval_status := 'AUTO_APPROVED';
        NEW.approved_by := NEW.created_by;
        NEW.approved_at := NOW();
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply auto-approve trigger to all data tables
CREATE TRIGGER trigger_cliente_auto_approve
    BEFORE INSERT ON cliente FOR EACH ROW EXECUTE FUNCTION check_auto_approve();

CREATE TRIGGER trigger_conta_programa_auto_approve
    BEFORE INSERT ON conta_programa FOR EACH ROW EXECUTE FUNCTION check_auto_approve();

CREATE TRIGGER trigger_transacao_auto_approve
    BEFORE INSERT ON transacao FOR EACH ROW EXECUTE FUNCTION check_auto_approve();

CREATE TRIGGER trigger_credencial_auto_approve
    BEFORE INSERT ON credencial_programa FOR EACH ROW EXECUTE FUNCTION check_auto_approve();

CREATE TRIGGER trigger_viagem_auto_approve
    BEFORE INSERT ON viagem FOR EACH ROW EXECUTE FUNCTION check_auto_approve();

-- ============================================================================
-- FUNCTION: Recalculate conta_programa on approved transactions
-- ============================================================================

CREATE OR REPLACE FUNCTION recalculate_conta_on_transacao_approval()
RETURNS TRIGGER AS $$
DECLARE
    v_conta conta_programa%ROWTYPE;
    v_custo_removido DECIMAL(15, 2);
    v_novo_saldo BIGINT;
    v_novo_custo_base DECIMAL(15, 2);
    v_novo_custo_medio DECIMAL(15, 6);
BEGIN
    -- Only process when status changes to APPROVED or AUTO_APPROVED
    IF NEW.approval_status NOT IN ('APPROVED', 'AUTO_APPROVED') THEN
        RETURN NEW;
    END IF;

    IF OLD.approval_status IN ('APPROVED', 'AUTO_APPROVED') THEN
        RETURN NEW;  -- Already processed
    END IF;

    -- Get current conta state
    SELECT * INTO v_conta FROM conta_programa WHERE id = NEW.conta_programa_id;

    IF NEW.tipo = 'COMPRA' THEN
        v_novo_saldo := v_conta.saldo_milhas + NEW.milhas;
        v_novo_custo_base := v_conta.custo_base_total_brl + NEW.valor_brl;
    ELSIF NEW.tipo = 'BONUS' THEN
        v_novo_saldo := v_conta.saldo_milhas + NEW.milhas;
        v_novo_custo_base := v_conta.custo_base_total_brl;  -- No cost change
    ELSIF NEW.tipo = 'VENDA' THEN
        IF v_conta.saldo_milhas = NEW.milhas THEN
            v_custo_removido := v_conta.custo_base_total_brl;
        ELSE
            v_custo_removido := (NEW.milhas::DECIMAL / v_conta.saldo_milhas) * v_conta.custo_base_total_brl;
        END IF;
        v_novo_saldo := v_conta.saldo_milhas - NEW.milhas;
        v_novo_custo_base := v_conta.custo_base_total_brl - v_custo_removido;

        IF v_novo_saldo = 0 THEN
            v_novo_custo_base := 0;
        END IF;
    END IF;

    -- Calculate new average cost
    IF v_novo_saldo > 0 AND v_novo_custo_base > 0 THEN
        v_novo_custo_medio := v_novo_custo_base / (v_novo_saldo::DECIMAL / 1000);
    ELSE
        v_novo_custo_medio := 0;
    END IF;

    -- Update conta
    UPDATE conta_programa
    SET saldo_milhas = v_novo_saldo,
        custo_base_total_brl = v_novo_custo_base,
        custo_medio_milheiro_atual = v_novo_custo_medio,
        updated_at = NOW()
    WHERE id = NEW.conta_programa_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_recalc_conta_on_approval
    AFTER UPDATE ON transacao
    FOR EACH ROW
    EXECUTE FUNCTION recalculate_conta_on_transacao_approval();

-- ============================================================================
-- GRANT PERMISSIONS TO APP ROLES
-- ============================================================================

GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_user;
GRANT SELECT ON v_cliente_approved TO app_user;
GRANT SELECT ON v_conta_programa_approved TO app_user;
GRANT SELECT ON v_transacao_approved TO app_user;
GRANT SELECT ON v_credencial_approved TO app_user;
GRANT SELECT ON v_viagem_approved TO app_user;

-- ============================================================================
-- SEED GLOBAL PROGRAM DATA
-- ============================================================================

INSERT INTO programa_milhas (account_id, brand, nome_completo, tipo, pais, status) VALUES
    (NULL, 'Smiles', 'Smiles GOL', 'AIRLINE', 'BR', 'ACTIVE'),
    (NULL, 'LATAM Pass', 'LATAM Pass', 'AIRLINE', 'BR', 'ACTIVE'),
    (NULL, 'Azul Fidelidade', 'TudoAzul', 'AIRLINE', 'BR', 'ACTIVE'),
    (NULL, 'Livelo', 'Livelo', 'BANK', 'BR', 'ACTIVE'),
    (NULL, 'Esfera', 'Esfera Santander', 'BANK', 'BR', 'ACTIVE'),
    (NULL, 'AAdvantage', 'American Airlines AAdvantage', 'AIRLINE', 'US', 'ACTIVE'),
    (NULL, 'MileagePlus', 'United MileagePlus', 'AIRLINE', 'US', 'ACTIVE'),
    (NULL, 'SkyMiles', 'Delta SkyMiles', 'AIRLINE', 'US', 'ACTIVE'),
    (NULL, 'Flying Blue', 'Air France-KLM Flying Blue', 'AIRLINE', 'FR', 'ACTIVE'),
    (NULL, 'TAP Miles&Go', 'TAP Miles&Go', 'AIRLINE', 'PT', 'ACTIVE'),
    (NULL, 'Iberia Plus', 'Iberia Plus', 'AIRLINE', 'ES', 'ACTIVE'),
    (NULL, 'Emirates Skywards', 'Emirates Skywards', 'AIRLINE', 'AE', 'ACTIVE'),
    (NULL, 'Qatar Privilege Club', 'Qatar Airways Privilege Club', 'AIRLINE', 'QA', 'ACTIVE'),
    (NULL, 'LifeMiles', 'Avianca LifeMiles', 'AIRLINE', 'CO', 'ACTIVE')
ON CONFLICT (account_id, brand) DO NOTHING;

-- ============================================================================
-- COMMENTS
-- ============================================================================

COMMENT ON TABLE programa_milhas IS 'Loyalty programs. NULL account_id = global/shared program.';
COMMENT ON TABLE cliente IS 'Customers belonging to an account. Requires approval workflow.';
COMMENT ON TABLE conta_programa IS 'Miles balance account linking customer to program.';
COMMENT ON TABLE transacao IS 'Immutable transaction log. Affects conta_programa on approval.';
COMMENT ON TABLE credencial_programa IS 'Encrypted credentials for external integrations.';
COMMENT ON TABLE saldo_milhas_externo IS 'External balance snapshots from API/scraping.';
COMMENT ON TABLE viagem IS 'Travel records and planning.';

COMMENT ON VIEW v_cliente_approved IS 'Only approved customers visible to all users.';
COMMENT ON VIEW v_conta_programa_approved IS 'Only approved accounts visible to all users.';
COMMENT ON VIEW v_transacao_approved IS 'Only approved transactions visible to all users.';
