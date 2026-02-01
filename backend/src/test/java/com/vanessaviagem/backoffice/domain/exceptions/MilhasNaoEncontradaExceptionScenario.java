package com.vanessaviagem.backoffice.domain.exceptions;

import java.util.UUID;

/**
 * Scenarios for testing MilhasNaoEncontradaException.
 */
public sealed interface MilhasNaoEncontradaExceptionScenario {

    String descricao();

    record CriacaoComIdScenario(
            String descricao,
            UUID milhasId,
            String expectedMessage
    ) implements MilhasNaoEncontradaExceptionScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }
}
