package com.vanessaviagem.backoffice.domain.exceptions;

import java.util.UUID;

/**
 * Exception thrown when a miles entry is not found in the system.
 *
 * <p>This exception is typically thrown when:
 * <ul>
 *   <li>Searching for a miles entry by ID that does not exist</li>
 *   <li>Attempting to update or delete a non-existent miles entry</li>
 * </ul>
 */
public class MilhasNaoEncontradaException extends DomainException {

    private final UUID milhasId;

    /**
     * Creates a new exception for miles not found.
     *
     * @param milhasId the ID of the miles entry that was not found
     */
    public MilhasNaoEncontradaException(UUID milhasId) {
        super(String.format("Milhas nao encontrada com id: %s", milhasId));
        this.milhasId = milhasId;
    }

    /**
     * Returns the ID of the miles entry that was not found.
     *
     * @return the miles ID
     */
    public UUID getMilhasId() {
        return milhasId;
    }
}
