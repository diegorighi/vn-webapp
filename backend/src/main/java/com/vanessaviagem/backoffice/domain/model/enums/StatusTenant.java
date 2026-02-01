package com.vanessaviagem.backoffice.domain.model.enums;

/**
 * Status do tenant no sistema multi-tenant.
 */
public enum StatusTenant {
    /**
     * Tenant ativo - pode realizar operações normalmente.
     */
    ATIVO,

    /**
     * Tenant suspenso temporariamente - acesso restrito.
     */
    SUSPENSO,

    /**
     * Tenant bloqueado - sem acesso ao sistema.
     */
    BLOQUEADO
}
