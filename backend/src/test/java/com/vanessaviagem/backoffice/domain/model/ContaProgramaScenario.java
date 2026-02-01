package com.vanessaviagem.backoffice.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Sealed interface containing all test scenario records for ContaPrograma tests.
 */
public sealed interface ContaProgramaScenario {

    String descricao();

    /**
     * Scenario for testing the criar factory method.
     */
    record CriarScenario(
            String descricao,
            UUID tenantId,
            UUID programaId,
            String programaNome,
            String owner
    ) implements ContaProgramaScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for testing invalid creation attempts.
     */
    record CriarInvalidoScenario(
            String descricao,
            UUID tenantId,
            UUID programaId,
            String programaNome,
            String owner,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements ContaProgramaScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for testing record constructor validation.
     */
    record ConstrutorInvalidoScenario(
            String descricao,
            UUID id,
            UUID tenantId,
            UUID programaId,
            String programaNome,
            String owner,
            long saldoMilhas,
            BigDecimal custoBaseTotalBRL,
            BigDecimal custoMedioMilheiroAtual,
            LocalDateTime criadoEm,
            LocalDateTime atualizadoEm,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements ContaProgramaScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for testing aplicarCompra.
     */
    record AplicarCompraScenario(
            String descricao,
            ContaPrograma contaInicial,
            long milhasCompradas,
            BigDecimal valorCompra,
            long saldoEsperado,
            BigDecimal custoBaseEsperado,
            BigDecimal custoMedioEsperado
    ) implements ContaProgramaScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for testing invalid aplicarCompra attempts.
     */
    record AplicarCompraInvalidoScenario(
            String descricao,
            ContaPrograma contaInicial,
            long milhasCompradas,
            BigDecimal valorCompra,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements ContaProgramaScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for testing aplicarBonus.
     */
    record AplicarBonusScenario(
            String descricao,
            ContaPrograma contaInicial,
            long milhasBonus,
            long saldoEsperado,
            BigDecimal custoBaseEsperado,
            BigDecimal custoMedioEsperado
    ) implements ContaProgramaScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for testing invalid aplicarBonus attempts.
     */
    record AplicarBonusInvalidoScenario(
            String descricao,
            ContaPrograma contaInicial,
            long milhasBonus,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements ContaProgramaScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for testing aplicarVenda.
     */
    record AplicarVendaScenario(
            String descricao,
            ContaPrograma contaInicial,
            long milhasVendidas,
            BigDecimal valorVenda,
            long saldoEsperado,
            BigDecimal custoBaseEsperado,
            BigDecimal custoMedioEsperado,
            BigDecimal custoRemovidoEsperado,
            BigDecimal lucroEsperado
    ) implements ContaProgramaScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for testing invalid aplicarVenda attempts.
     */
    record AplicarVendaInvalidoScenario(
            String descricao,
            ContaPrograma contaInicial,
            long milhasVendidas,
            BigDecimal valorVenda,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements ContaProgramaScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for testing temSaldo method.
     */
    record TemSaldoScenario(
            String descricao,
            ContaPrograma conta,
            boolean esperado
    ) implements ContaProgramaScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for testing podeSacar method.
     */
    record PodeSacarScenario(
            String descricao,
            ContaPrograma conta,
            long milhas,
            boolean esperado
    ) implements ContaProgramaScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for testing the complete flow: compra, bonus, venda.
     */
    record FluxoCompletoScenario(
            String descricao,
            UUID tenantId,
            UUID programaId,
            String programaNome,
            String owner,
            long milhasCompra1,
            BigDecimal valorCompra1,
            long milhasBonus,
            long milhasVenda,
            BigDecimal valorVenda,
            long saldoFinalEsperado,
            BigDecimal lucroEsperado
    ) implements ContaProgramaScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }
}
