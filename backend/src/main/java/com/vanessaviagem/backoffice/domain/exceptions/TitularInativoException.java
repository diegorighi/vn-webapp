package com.vanessaviagem.backoffice.domain.exceptions;

import java.util.UUID;

/**
 * Exception thrown when attempting to add a dependent to an inactive titular.
 * This enforces the business rule that dependents can only be added to active titulares.
 */
public class TitularInativoException extends DomainException {

    private final UUID titularId;

    /**
     * Creates a new exception for an inactive titular.
     *
     * @param titularId the ID of the inactive titular
     */
    public TitularInativoException(UUID titularId) {
        super(String.format("Titular inativo: %s. Nao e possivel adicionar dependente a titular inativo.", titularId));
        this.titularId = titularId;
    }

    /**
     * Creates a new exception with a custom message.
     *
     * @param titularId the ID of the inactive titular
     * @param message a custom detail message
     */
    public TitularInativoException(UUID titularId, String message) {
        super(message);
        this.titularId = titularId;
    }

    /**
     * Returns the ID of the inactive titular.
     *
     * @return the titular ID
     */
    public UUID getTitularId() {
        return titularId;
    }
}
