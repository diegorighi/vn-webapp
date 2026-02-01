-- =============================================================================
-- V006: Criação da tabela de sessões de programas de milhas
-- =============================================================================

CREATE TABLE programa_session (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenant(tenant_id),
    credencial_id UUID NOT NULL REFERENCES credencial_programa(id) ON DELETE CASCADE,
    programa VARCHAR(50) NOT NULL,
    session_token TEXT,
    cookies JSONB DEFAULT '{}',
    user_agent VARCHAR(500),
    device_fingerprint VARCHAR(100),
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
    expira_em TIMESTAMP,
    ultimo_uso TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Índices
CREATE INDEX idx_session_tenant ON programa_session(tenant_id);
CREATE INDEX idx_session_credencial ON programa_session(credencial_id);
CREATE INDEX idx_session_expira ON programa_session(expira_em);
CREATE UNIQUE INDEX idx_session_credencial_unique ON programa_session(credencial_id);

COMMENT ON TABLE programa_session IS 'Sessões ativas de programas de milhas para evitar re-autenticação';
COMMENT ON COLUMN programa_session.cookies IS 'Cookies de sessão em formato JSON';
COMMENT ON COLUMN programa_session.device_fingerprint IS 'Fingerprint do dispositivo para trusted device';

-- RLS
ALTER TABLE programa_session ENABLE ROW LEVEL SECURITY;

CREATE POLICY tenant_isolation_session ON programa_session
    FOR ALL
    USING (tenant_id = current_setting('app.current_tenant', true)::uuid)
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true)::uuid);

ALTER TABLE programa_session FORCE ROW LEVEL SECURITY;
