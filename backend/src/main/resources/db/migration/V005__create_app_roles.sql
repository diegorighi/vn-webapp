-- =============================================================================
-- V005: Criação de roles e permissões para a aplicação
-- =============================================================================

-- Role para a aplicação (NÃO pode bypassar RLS)
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'app_user') THEN
        CREATE ROLE app_user NOINHERIT NOLOGIN;
    END IF;
END
$$;

-- Permissões para app_user
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_user;

-- Garantir que RLS está habilitado para app_user
ALTER ROLE app_user SET row_security = on;

-- Role admin (PODE bypassar RLS para operações cross-tenant)
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'app_admin') THEN
        CREATE ROLE app_admin BYPASSRLS NOLOGIN;
    END IF;
END
$$;

-- Permissões para app_admin
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO app_admin;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_admin;

-- Comentários
COMMENT ON ROLE app_user IS 'Role para operações normais da aplicação - respeitaRLS';
COMMENT ON ROLE app_admin IS 'Role administrativa - bypassa RLS para operações cross-tenant';
