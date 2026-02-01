-- =============================================================================
-- V004: Criação da tabela de logs de auditoria
-- =============================================================================

CREATE TABLE audit_log (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenant(tenant_id),
    user_id UUID NOT NULL,
    acao VARCHAR(50) NOT NULL,
    recurso VARCHAR(50) NOT NULL,
    recurso_id UUID,
    ip_origem VARCHAR(45),
    user_agent VARCHAR(500),
    detalhes JSONB DEFAULT '{}',
    sucesso BOOLEAN NOT NULL DEFAULT true,
    mensagem_erro TEXT,
    timestamp TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Índices para consultas frequentes
CREATE INDEX idx_audit_tenant ON audit_log(tenant_id);
CREATE INDEX idx_audit_timestamp ON audit_log(timestamp DESC);
CREATE INDEX idx_audit_acao ON audit_log(acao);
CREATE INDEX idx_audit_recurso ON audit_log(recurso, recurso_id);
CREATE INDEX idx_audit_tenant_timestamp ON audit_log(tenant_id, timestamp DESC);
CREATE INDEX idx_audit_tenant_acao ON audit_log(tenant_id, acao, timestamp DESC);

-- Índice para consultas de segurança (acessos não autorizados)
CREATE INDEX idx_audit_unauthorized ON audit_log(tenant_id, timestamp DESC)
    WHERE acao = 'UNAUTHORIZED_ACCESS';

-- Índice para consultas de falhas
CREATE INDEX idx_audit_failures ON audit_log(tenant_id, timestamp DESC)
    WHERE sucesso = false;

COMMENT ON TABLE audit_log IS 'Logs de auditoria - append-only e imutáveis';
COMMENT ON COLUMN audit_log.acao IS 'Tipo de ação: CREDENTIAL_READ, BALANCE_QUERY, UNAUTHORIZED_ACCESS, etc.';
COMMENT ON COLUMN audit_log.recurso IS 'Tipo de recurso: credencial_programa, saldo_milhas, etc.';
COMMENT ON COLUMN audit_log.detalhes IS 'Detalhes adicionais em formato JSON (sem dados sensíveis)';

-- =============================================================================
-- Habilitar Row-Level Security
-- =============================================================================

ALTER TABLE audit_log ENABLE ROW LEVEL SECURITY;

CREATE POLICY tenant_isolation_audit ON audit_log
    FOR ALL
    USING (tenant_id = current_setting('app.current_tenant', true)::uuid)
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true)::uuid);

ALTER TABLE audit_log FORCE ROW LEVEL SECURITY;

-- =============================================================================
-- Trigger para garantir imutabilidade (append-only)
-- =============================================================================

CREATE OR REPLACE FUNCTION prevent_audit_modification()
RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION 'Logs de auditoria são imutáveis e não podem ser modificados ou excluídos';
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER audit_log_immutable
    BEFORE UPDATE OR DELETE ON audit_log
    FOR EACH ROW
    EXECUTE FUNCTION prevent_audit_modification();
