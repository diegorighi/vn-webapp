package com.vanessaviagem.backoffice.domain.exceptions;

import java.util.UUID;

/**
 * Exceção lançada quando uma credencial de programa de milhas não é encontrada.
 */
public class CredencialNaoEncontradaException extends DomainException {

    private final UUID credencialId;

    public CredencialNaoEncontradaException(UUID credencialId) {
        super("Credencial não encontrada: " + credencialId);
        this.credencialId = credencialId;
    }

    public UUID getCredencialId() {
        return credencialId;
    }
}
