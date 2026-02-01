package com.vanessaviagem.backoffice.domain.model.enums;

/**
 * Types of operations that require approval.
 */
public enum OperationType {
    /**
     * Creating new data.
     */
    INSERT,

    /**
     * Modifying existing data.
     */
    UPDATE,

    /**
     * Removing data.
     */
    DELETE
}
