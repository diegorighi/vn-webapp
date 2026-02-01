package com.vanessaviagem.backoffice.domain.exceptions;

import java.util.UUID;

public record ClienteNaoEncontradoExceptionScenario(
        String descricao,
        UUID clienteId,
        String customMessage,
        String expectedMessagePattern
) {
    @Override
    public String toString() {
        return descricao;
    }
}
