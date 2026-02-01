package com.vanessaviagem.backoffice.domain.exceptions;

/**
 * Base exception class for all domain-level exceptions.
 * Domain exceptions represent business rule violations or
 * invalid state transitions within the domain model.
 *
 * <p>This is an unchecked exception (extends RuntimeException)
 * to avoid polluting domain code with checked exception handling.</p>
 */
public class DomainException extends RuntimeException {

    /**
     * Creates a new domain exception with the specified message.
     *
     * @param message the detail message explaining the domain violation
     */
    public DomainException(String message) {
        super(message);
    }

    /**
     * Creates a new domain exception with the specified message and cause.
     *
     * @param message the detail message explaining the domain violation
     * @param cause the underlying cause of this exception
     */
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
