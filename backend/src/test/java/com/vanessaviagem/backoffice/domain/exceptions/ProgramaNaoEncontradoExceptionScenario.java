package com.vanessaviagem.backoffice.domain.exceptions;

import java.util.UUID;

/**
 * Scenarios for testing ProgramaNaoEncontradoException.
 */
public sealed interface ProgramaNaoEncontradoExceptionScenario {

    String descricao();

    record CriacaoComIdScenario(
            String descricao,
            UUID programaId,
            String expectedMessage
    ) implements ProgramaNaoEncontradoExceptionScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record CriacaoComBrandScenario(
            String descricao,
            String brand,
            String expectedMessage
    ) implements ProgramaNaoEncontradoExceptionScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record CriacaoComIdEMensagemScenario(
            String descricao,
            UUID programaId,
            String customMessage,
            String expectedMessage
    ) implements ProgramaNaoEncontradoExceptionScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }
}
