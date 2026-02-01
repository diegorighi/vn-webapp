package com.vanessaviagem.backoffice.domain.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Registro de auditoria para rastreamento de operações sensíveis.
 * Logs de auditoria são imutáveis e append-only.
 *
 * <p>IMPORTANTE: Todos os acessos a credenciais e dados sensíveis
 * DEVEM ser registrados nesta tabela.</p>
 */
public record AuditLog(
        UUID id,
        UUID tenantId,
        UUID userId,
        String acao,
        String recurso,
        UUID recursoId,
        String ipOrigem,
        String userAgent,
        Map<String, Object> detalhes,
        boolean sucesso,
        String mensagemErro,
        LocalDateTime timestamp
) {
    /**
     * Ações de auditoria padronizadas.
     */
    public static final class Acoes {
        public static final String CREDENTIAL_CREATE = "CREDENTIAL_CREATE";
        public static final String CREDENTIAL_READ = "CREDENTIAL_READ";
        public static final String CREDENTIAL_UPDATE = "CREDENTIAL_UPDATE";
        public static final String CREDENTIAL_DELETE = "CREDENTIAL_DELETE";
        public static final String CREDENTIAL_DECRYPT = "CREDENTIAL_DECRYPT";
        public static final String BALANCE_QUERY = "BALANCE_QUERY";
        public static final String BALANCE_QUERY_FAILED = "BALANCE_QUERY_FAILED";
        public static final String UNAUTHORIZED_ACCESS = "UNAUTHORIZED_ACCESS";
        public static final String TOKEN_REFRESH = "TOKEN_REFRESH";
        public static final String LOGIN_SUCCESS = "LOGIN_SUCCESS";
        public static final String LOGIN_FAILED = "LOGIN_FAILED";

        private Acoes() {
        }
    }

    /**
     * Recursos de auditoria padronizados.
     */
    public static final class Recursos {
        public static final String CREDENCIAL_PROGRAMA = "credencial_programa";
        public static final String SALDO_MILHAS = "saldo_milhas";
        public static final String CLIENTE = "cliente";
        public static final String TENANT = "tenant";

        private Recursos() {
        }
    }

    public AuditLog {
        Objects.requireNonNull(id, "id é obrigatório");
        Objects.requireNonNull(tenantId, "tenantId é obrigatório");
        Objects.requireNonNull(userId, "userId é obrigatório");
        Objects.requireNonNull(acao, "acao é obrigatório");
        Objects.requireNonNull(recurso, "recurso é obrigatório");
        Objects.requireNonNull(timestamp, "timestamp é obrigatório");

        if (acao.isBlank()) {
            throw new IllegalArgumentException("acao não pode ser vazia");
        }
        if (recurso.isBlank()) {
            throw new IllegalArgumentException("recurso não pode ser vazio");
        }

        detalhes = detalhes != null ? Map.copyOf(detalhes) : Map.of();
    }

    /**
     * Cria um log de auditoria de sucesso.
     */
    public static AuditLog sucesso(
            UUID tenantId,
            UUID userId,
            String acao,
            String recurso,
            UUID recursoId,
            Map<String, Object> detalhes
    ) {
        return new AuditLog(
                UUID.randomUUID(),
                tenantId,
                userId,
                acao,
                recurso,
                recursoId,
                null,
                null,
                detalhes,
                true,
                null,
                LocalDateTime.now()
        );
    }

    /**
     * Cria um log de auditoria de sucesso com contexto HTTP.
     */
    public static AuditLog sucessoComContexto(
            UUID tenantId,
            UUID userId,
            String acao,
            String recurso,
            UUID recursoId,
            String ipOrigem,
            String userAgent,
            Map<String, Object> detalhes
    ) {
        return new AuditLog(
                UUID.randomUUID(),
                tenantId,
                userId,
                acao,
                recurso,
                recursoId,
                ipOrigem,
                userAgent,
                detalhes,
                true,
                null,
                LocalDateTime.now()
        );
    }

    /**
     * Cria um log de auditoria de falha.
     */
    public static AuditLog falha(
            UUID tenantId,
            UUID userId,
            String acao,
            String recurso,
            UUID recursoId,
            String mensagemErro,
            Map<String, Object> detalhes
    ) {
        return new AuditLog(
                UUID.randomUUID(),
                tenantId,
                userId,
                acao,
                recurso,
                recursoId,
                null,
                null,
                detalhes,
                false,
                mensagemErro,
                LocalDateTime.now()
        );
    }

    /**
     * Cria um log de auditoria de tentativa de acesso não autorizado.
     */
    public static AuditLog acessoNaoAutorizado(
            UUID tenantId,
            UUID userId,
            String recurso,
            UUID recursoId,
            String ipOrigem,
            Map<String, Object> detalhes
    ) {
        return new AuditLog(
                UUID.randomUUID(),
                tenantId,
                userId,
                Acoes.UNAUTHORIZED_ACCESS,
                recurso,
                recursoId,
                ipOrigem,
                null,
                detalhes,
                false,
                "Tentativa de acesso a recurso de outro tenant",
                LocalDateTime.now()
        );
    }
}
