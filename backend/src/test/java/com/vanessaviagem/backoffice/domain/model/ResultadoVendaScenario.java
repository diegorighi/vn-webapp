package com.vanessaviagem.backoffice.domain.model;

import java.math.BigDecimal;

/**
 * Sealed interface containing all test scenario records for ResultadoVenda tests.
 */
public sealed interface ResultadoVendaScenario {

    String descricao();

    /**
     * Scenario for testing valid ResultadoVenda creation.
     */
    record CriacaoValidaScenario(
            String descricao,
            ContaPrograma contaAtualizada,
            BigDecimal custoRemovido,
            BigDecimal lucro
    ) implements ResultadoVendaScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for testing profit/loss check.
     */
    record LucroOuPrejuizoScenario(
            String descricao,
            BigDecimal lucro,
            boolean teveLucro,
            boolean tevePrejuizo
    ) implements ResultadoVendaScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }
}
