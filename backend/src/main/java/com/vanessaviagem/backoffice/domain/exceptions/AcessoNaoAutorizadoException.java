package com.vanessaviagem.backoffice.domain.exceptions;

import java.util.UUID;

/**
 * Exceção lançada quando há uma tentativa de acesso não autorizado a recursos de outro tenant.
 */
public class AcessoNaoAutorizadoException extends DomainException {

    private final UUID tenantSolicitante;
    private final UUID recursoId;
    private final String recursoTipo;

    public AcessoNaoAutorizadoException(UUID tenantSolicitante, UUID recursoId, String recursoTipo) {
        super(String.format("Acesso não autorizado ao recurso %s [%s] pelo tenant %s",
                recursoTipo, recursoId, tenantSolicitante));
        this.tenantSolicitante = tenantSolicitante;
        this.recursoId = recursoId;
        this.recursoTipo = recursoTipo;
    }

    public UUID getTenantSolicitante() {
        return tenantSolicitante;
    }

    public UUID getRecursoId() {
        return recursoId;
    }

    public String getRecursoTipo() {
        return recursoTipo;
    }
}
