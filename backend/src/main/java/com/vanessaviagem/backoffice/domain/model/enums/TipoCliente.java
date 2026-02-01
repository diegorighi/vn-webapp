package com.vanessaviagem.backoffice.domain.model.enums;

/**
 * Tipo de cliente no sistema.
 * TITULAR: cliente principal, pode ter dependentes
 * DEPENDENTE: vinculado a um titular
 */
public enum TipoCliente {
    TITULAR,
    DEPENDENTE
}
