package com.vanessaviagem.backoffice.domain.exceptions;

import java.util.UUID;

/**
 * Exception thrown when a client (titular or dependent) cannot be found.
 * This typically occurs when attempting to perform operations on a
 * non-existent client ID.
 */
public class ClienteNaoEncontradoException extends DomainException {

    private final UUID clienteId;

    /**
     * Creates a new exception for a client not found by ID.
     *
     * @param clienteId the ID of the client that was not found
     */
    public ClienteNaoEncontradoException(UUID clienteId) {
        super(String.format("Cliente nao encontrado: %s", clienteId));
        this.clienteId = clienteId;
    }

    /**
     * Creates a new exception with a custom message.
     *
     * @param clienteId the ID of the client that was not found
     * @param message a custom detail message
     */
    public ClienteNaoEncontradoException(UUID clienteId, String message) {
        super(message);
        this.clienteId = clienteId;
    }

    /**
     * Returns the ID of the client that was not found.
     *
     * @return the client ID
     */
    public UUID getClienteId() {
        return clienteId;
    }
}
