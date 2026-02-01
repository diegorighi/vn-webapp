package com.vanessaviagem.backoffice.application.services;

import com.vanessaviagem.backoffice.domain.model.Milhas;
import com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Scenario records for MilhasService parameterized tests.
 * Each scenario encapsulates all data needed for a specific test case.
 */
public sealed interface MilhasServiceScenarios {

    String descricao();

    /**
     * Scenario for registrarMilhas operation.
     */
    record RegistrarMilhasScenario(
            String descricao,
            UUID clienteId,
            Milhas milhasInput,
            Milhas expectedResult,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements MilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for buscarMilhas operation.
     */
    record BuscarMilhasScenario(
            String descricao,
            UUID milhasId,
            Optional<Milhas> repositoryResponse,
            Optional<Milhas> expectedResult
    ) implements MilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for buscarMilhasPorCliente operation.
     */
    record BuscarMilhasPorClienteScenario(
            String descricao,
            UUID clienteId,
            List<Milhas> repositoryResponse,
            int expectedSize
    ) implements MilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for buscarMilhasPorPrograma operation.
     */
    record BuscarMilhasPorProgramaScenario(
            String descricao,
            TipoProgramaMilhas programa,
            List<Milhas> repositoryResponse,
            int expectedSize
    ) implements MilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for atualizarMilhas operation.
     */
    record AtualizarMilhasScenario(
            String descricao,
            Milhas milhasInput,
            boolean existsInRepository,
            Milhas repositorySaveResult,
            Milhas expectedResult,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements MilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for removerMilhas operation.
     */
    record RemoverMilhasScenario(
            String descricao,
            UUID milhasId,
            boolean existsInRepository,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements MilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for calcularSaldoTotal operation.
     */
    record CalcularSaldoTotalScenario(
            String descricao,
            UUID clienteId,
            List<Milhas> repositoryResponse,
            int expectedSaldoTotal
    ) implements MilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for calcularCustoMedioMilheiro operation.
     */
    record CalcularCustoMedioMilheiroScenario(
            String descricao,
            UUID clienteId,
            List<Milhas> repositoryResponse,
            BigDecimal expectedCustoMedio
    ) implements MilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }
}
