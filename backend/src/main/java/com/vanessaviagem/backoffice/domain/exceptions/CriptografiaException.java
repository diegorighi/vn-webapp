package com.vanessaviagem.backoffice.domain.exceptions;

/**
 * Exceção lançada quando ocorre um erro de criptografia/descriptografia.
 */
public class CriptografiaException extends DomainException {

    public CriptografiaException(String message) {
        super(message);
    }

    public CriptografiaException(String message, Throwable cause) {
        super(message, cause);
    }
}
