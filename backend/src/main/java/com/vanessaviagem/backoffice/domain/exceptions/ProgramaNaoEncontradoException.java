package com.vanessaviagem.backoffice.domain.exceptions;

import java.util.UUID;

/**
 * Exception thrown when a loyalty program cannot be found.
 * This typically occurs when attempting to perform operations on a
 * non-existent program ID or brand.
 */
public class ProgramaNaoEncontradoException extends DomainException {

    private final UUID programaId;
    private final String brand;

    /**
     * Creates a new exception for a program not found by ID.
     *
     * @param programaId the ID of the program that was not found
     */
    public ProgramaNaoEncontradoException(UUID programaId) {
        super(String.format("Programa de milhas nao encontrado: %s", programaId));
        this.programaId = programaId;
        this.brand = null;
    }

    /**
     * Creates a new exception for a program not found by brand.
     *
     * @param brand the brand of the program that was not found
     */
    public ProgramaNaoEncontradoException(String brand) {
        super(String.format("Programa de milhas nao encontrado para brand: %s", brand));
        this.programaId = null;
        this.brand = brand;
    }

    /**
     * Creates a new exception with a custom message.
     *
     * @param programaId the ID of the program that was not found
     * @param message a custom detail message
     */
    public ProgramaNaoEncontradoException(UUID programaId, String message) {
        super(message);
        this.programaId = programaId;
        this.brand = null;
    }

    /**
     * Returns the ID of the program that was not found.
     *
     * @return the program ID, or null if searched by brand
     */
    public UUID getProgramaId() {
        return programaId;
    }

    /**
     * Returns the brand of the program that was not found.
     *
     * @return the brand name, or null if searched by ID
     */
    public String getBrand() {
        return brand;
    }
}
