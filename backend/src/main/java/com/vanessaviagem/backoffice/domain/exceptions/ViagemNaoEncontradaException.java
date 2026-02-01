package com.vanessaviagem.backoffice.domain.exceptions;

import java.util.UUID;

/**
 * Exception thrown when a viagem (trip) cannot be found.
 * This typically occurs when attempting to perform operations on a
 * non-existent viagem ID.
 */
public class ViagemNaoEncontradaException extends DomainException {

    private final UUID viagemId;

    /**
     * Creates a new exception for a viagem not found by ID.
     *
     * @param viagemId the ID of the viagem that was not found
     */
    public ViagemNaoEncontradaException(UUID viagemId) {
        super(String.format("Viagem nao encontrada: %s", viagemId));
        this.viagemId = viagemId;
    }

    /**
     * Creates a new exception with a custom message.
     *
     * @param viagemId the ID of the viagem that was not found
     * @param message a custom detail message
     */
    public ViagemNaoEncontradaException(UUID viagemId, String message) {
        super(message);
        this.viagemId = viagemId;
    }

    /**
     * Returns the ID of the viagem that was not found.
     *
     * @return the viagem ID
     */
    public UUID getViagemId() {
        return viagemId;
    }
}
