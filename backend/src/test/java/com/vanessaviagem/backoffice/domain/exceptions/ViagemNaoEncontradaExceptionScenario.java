package com.vanessaviagem.backoffice.domain.exceptions;

import java.util.UUID;

/**
 * Scenarios for testing ViagemNaoEncontradaException.
 */
public sealed interface ViagemNaoEncontradaExceptionScenario {

    String descricao();

    record CriacaoComIdScenario(
            String descricao,
            UUID viagemId,
            String expectedMessage
    ) implements ViagemNaoEncontradaExceptionScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record CriacaoComIdEMensagemScenario(
            String descricao,
            UUID viagemId,
            String customMessage,
            String expectedMessage
    ) implements ViagemNaoEncontradaExceptionScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }
}
