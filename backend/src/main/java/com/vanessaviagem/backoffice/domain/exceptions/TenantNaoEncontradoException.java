package com.vanessaviagem.backoffice.domain.exceptions;

import java.util.UUID;

/**
 * Exceção lançada quando um tenant não é encontrado.
 */
public class TenantNaoEncontradoException extends DomainException {

    private final UUID tenantId;

    public TenantNaoEncontradoException(UUID tenantId) {
        super("Tenant não encontrado: " + tenantId);
        this.tenantId = tenantId;
    }

    public UUID getTenantId() {
        return tenantId;
    }
}
