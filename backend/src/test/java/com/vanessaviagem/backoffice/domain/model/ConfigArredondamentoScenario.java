package com.vanessaviagem.backoffice.domain.model;

import java.math.RoundingMode;

/**
 * Scenarios for testing ConfigArredondamento value object.
 */
public sealed interface ConfigArredondamentoScenario {

    String descricao();

    record CriacaoValidaScenario(
            String descricao,
            int casasDecimais,
            RoundingMode modoArredondamento
    ) implements ConfigArredondamentoScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record CriacaoInvalidaScenario(
            String descricao,
            int casasDecimais,
            RoundingMode modoArredondamento,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements ConfigArredondamentoScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record ComCasasDecimaisScenario(
            String descricao,
            int casasDecimais,
            RoundingMode expectedRoundingMode
    ) implements ConfigArredondamentoScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record DefaultConstantScenario(
            String descricao,
            int expectedCasasDecimais,
            RoundingMode expectedModoArredondamento
    ) implements ConfigArredondamentoScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }
}
