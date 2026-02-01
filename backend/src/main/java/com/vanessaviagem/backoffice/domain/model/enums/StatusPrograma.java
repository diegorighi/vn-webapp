package com.vanessaviagem.backoffice.domain.model.enums;

/**
 * Represents the status of a loyalty program.
 * A program can be active (ATIVO) or inactive (INATIVO).
 */
public enum StatusPrograma {
    /**
     * Program is active and can be used for transactions.
     */
    ATIVO,

    /**
     * Program is inactive and should not accept new transactions.
     */
    INATIVO
}
