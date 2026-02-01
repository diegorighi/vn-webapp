-- =============================================================================
-- V009: Criacao da tabela transacao (append-only, imutavel)
-- =============================================================================

CREATE TABLE transacao (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenant(tenant_id),
    conta_programa_id UUID NOT NULL REFERENCES conta_programa(id),
    tipo VARCHAR(20) NOT NULL,
    milhas BIGINT NOT NULL,
    valor_brl DECIMAL(15, 4) NOT NULL DEFAULT 0,
    fonte VARCHAR(100),
    observacao TEXT,
    data TIMESTAMP NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_transacao_tipo CHECK (tipo IN ('COMPRA', 'VENDA', 'BONUS')),
    CONSTRAINT chk_transacao_milhas_positivo CHECK (milhas > 0),
    CONSTRAINT chk_transacao_valor_positivo CHECK (valor_brl >= 0)
);

-- Indices para consultas frequentes
CREATE INDEX idx_transacao_tenant ON transacao(tenant_id);
CREATE INDEX idx_transacao_conta ON transacao(conta_programa_id);
CREATE INDEX idx_transacao_data ON transacao(data DESC);
CREATE INDEX idx_transacao_tipo ON transacao(tipo);
CREATE INDEX idx_transacao_conta_data ON transacao(conta_programa_id, data DESC);
CREATE INDEX idx_transacao_tenant_data ON transacao(tenant_id, data DESC);

-- =============================================================================
-- Habilitar Row-Level Security
-- =============================================================================

ALTER TABLE transacao ENABLE ROW LEVEL SECURITY;

CREATE POLICY tenant_isolation_transacao ON transacao
    FOR ALL
    USING (tenant_id = current_setting('app.current_tenant', true)::uuid)
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true)::uuid);

ALTER TABLE transacao FORCE ROW LEVEL SECURITY;

-- Comentarios
COMMENT ON TABLE transacao IS 'Registro de transacoes de milhas (append-only, imutavel)';
COMMENT ON COLUMN transacao.conta_programa_id IS 'Referencia para a conta do programa de milhas';
COMMENT ON COLUMN transacao.tipo IS 'Tipo da transacao: COMPRA, VENDA ou BONUS';
COMMENT ON COLUMN transacao.milhas IS 'Quantidade de milhas movimentadas (sempre > 0)';
COMMENT ON COLUMN transacao.valor_brl IS 'Valor em BRL da transacao';
COMMENT ON COLUMN transacao.fonte IS 'Origem do bonus/cashback (ex: cartao credito, parceiro)';
COMMENT ON COLUMN transacao.observacao IS 'Observacao adicional (usado para ajustes manuais)';
COMMENT ON COLUMN transacao.data IS 'Data/hora da transacao (informada pelo usuario)';
COMMENT ON COLUMN transacao.criado_em IS 'Data/hora de criacao do registro (automatico)';
