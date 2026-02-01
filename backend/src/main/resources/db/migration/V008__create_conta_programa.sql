-- =============================================================================
-- V008: Criacao da tabela conta_programa (agregado de milhas por programa)
-- =============================================================================

CREATE TABLE conta_programa (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenant(tenant_id),
    programa_id UUID NOT NULL,
    programa_nome VARCHAR(100) NOT NULL,
    owner VARCHAR(100) NOT NULL,
    saldo_milhas BIGINT NOT NULL DEFAULT 0,
    custo_base_total_brl DECIMAL(15, 4) NOT NULL DEFAULT 0,
    custo_medio_milheiro_atual DECIMAL(15, 6) NOT NULL DEFAULT 0,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_conta_saldo_positivo CHECK (saldo_milhas >= 0),
    CONSTRAINT chk_conta_custo_base_positivo CHECK (custo_base_total_brl >= 0),
    CONSTRAINT chk_conta_custo_medio_positivo CHECK (custo_medio_milheiro_atual >= 0),
    CONSTRAINT uq_conta_tenant_programa_owner UNIQUE (tenant_id, programa_id, owner)
);

-- Indices para consultas frequentes
CREATE INDEX idx_conta_programa_tenant ON conta_programa(tenant_id);
CREATE INDEX idx_conta_programa_owner ON conta_programa(tenant_id, owner);
CREATE INDEX idx_conta_programa_programa ON conta_programa(tenant_id, programa_id);
CREATE INDEX idx_conta_programa_nome ON conta_programa(tenant_id, programa_nome);

-- =============================================================================
-- Habilitar Row-Level Security
-- =============================================================================

ALTER TABLE conta_programa ENABLE ROW LEVEL SECURITY;

CREATE POLICY tenant_isolation_conta_programa ON conta_programa
    FOR ALL
    USING (tenant_id = current_setting('app.current_tenant', true)::uuid)
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true)::uuid);

ALTER TABLE conta_programa FORCE ROW LEVEL SECURITY;

-- Comentarios
COMMENT ON TABLE conta_programa IS 'Agregado de conta de milhas por programa e proprietario';
COMMENT ON COLUMN conta_programa.programa_id IS 'ID do programa de milhas (Smiles, LATAM Pass, etc)';
COMMENT ON COLUMN conta_programa.programa_nome IS 'Nome do programa para exibicao';
COMMENT ON COLUMN conta_programa.owner IS 'Proprietario das milhas (Diego, Vanessa, etc)';
COMMENT ON COLUMN conta_programa.saldo_milhas IS 'Saldo atual de milhas (sempre >= 0)';
COMMENT ON COLUMN conta_programa.custo_base_total_brl IS 'Custo total investido em BRL (base para calculo do custo medio)';
COMMENT ON COLUMN conta_programa.custo_medio_milheiro_atual IS 'Custo medio por milheiro em BRL (custo_base / saldo * 1000)';
