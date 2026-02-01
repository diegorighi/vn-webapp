-- Tabela para registro manual de saldos de milhas
CREATE TABLE saldo_milhas (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    programa VARCHAR(100) NOT NULL,
    owner VARCHAR(100) NOT NULL,
    quantidade BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_saldo_milhas_tenant_programa_owner UNIQUE (tenant_id, programa, owner),
    CONSTRAINT chk_quantidade_positiva CHECK (quantidade >= 0)
);

-- Índices para consultas comuns
CREATE INDEX idx_saldo_milhas_tenant ON saldo_milhas(tenant_id);
CREATE INDEX idx_saldo_milhas_owner ON saldo_milhas(tenant_id, owner);
CREATE INDEX idx_saldo_milhas_programa ON saldo_milhas(tenant_id, programa);

-- Row Level Security
ALTER TABLE saldo_milhas ENABLE ROW LEVEL SECURITY;

CREATE POLICY tenant_isolation_saldo_milhas ON saldo_milhas
    FOR ALL
    USING (tenant_id = current_setting('app.current_tenant', true)::uuid)
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true)::uuid);

-- Comentários
COMMENT ON TABLE saldo_milhas IS 'Registro manual de saldos de milhas por programa e proprietário';
COMMENT ON COLUMN saldo_milhas.programa IS 'Nome do programa (Smiles, LATAM Pass, Livelo, etc)';
COMMENT ON COLUMN saldo_milhas.owner IS 'Nome do proprietário das milhas (Diego, Vanessa, etc)';
COMMENT ON COLUMN saldo_milhas.quantidade IS 'Quantidade de milhas/pontos';
