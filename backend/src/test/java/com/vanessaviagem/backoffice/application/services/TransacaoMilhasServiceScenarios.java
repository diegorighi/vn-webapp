package com.vanessaviagem.backoffice.application.services;

import com.vanessaviagem.backoffice.application.ports.in.RegistrarTransacaoUseCase.RegistrarBonusCommand;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarTransacaoUseCase.RegistrarCompraCommand;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarTransacaoUseCase.RegistrarVendaCommand;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarTransacaoUseCase.TransacaoResult;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarTransacaoUseCase.VendaResult;
import com.vanessaviagem.backoffice.domain.model.ContaPrograma;
import com.vanessaviagem.backoffice.domain.model.Transacao;
import com.vanessaviagem.backoffice.domain.model.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Scenario records for TransacaoMilhasService parameterized tests.
 * Each scenario encapsulates all data needed for a specific test case.
 */
public sealed interface TransacaoMilhasServiceScenarios {

    String descricao();

    // ==================== REGISTRAR COMPRA ====================

    /**
     * Scenario for registrarCompra operation.
     */
    record RegistrarCompraScenario(
            String descricao,
            RegistrarCompraCommand command,
            Optional<ContaPrograma> contaExistente,
            ContaPrograma contaSalvaOuAtualizada,
            Transacao transacaoSalva,
            boolean deveCriarConta,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements TransacaoMilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    // ==================== REGISTRAR BONUS ====================

    /**
     * Scenario for registrarBonus operation.
     */
    record RegistrarBonusScenario(
            String descricao,
            RegistrarBonusCommand command,
            Optional<ContaPrograma> contaExistente,
            ContaPrograma contaSalvaOuAtualizada,
            Transacao transacaoSalva,
            boolean deveCriarConta,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements TransacaoMilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    // ==================== REGISTRAR VENDA ====================

    /**
     * Scenario for registrarVenda operation.
     */
    record RegistrarVendaScenario(
            String descricao,
            RegistrarVendaCommand command,
            Optional<ContaPrograma> contaExistente,
            ContaPrograma contaAtualizada,
            Transacao transacaoSalva,
            BigDecimal lucroEsperado,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements TransacaoMilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    // ==================== BUSCAR POR ID ====================

    /**
     * Scenario for buscarPorId operation.
     */
    record BuscarPorIdScenario(
            String descricao,
            UUID tenantId,
            UUID contaId,
            Optional<ContaPrograma> repositoryResponse,
            boolean expectedPresent
    ) implements TransacaoMilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    // ==================== LISTAR POR OWNER ====================

    /**
     * Scenario for listarPorOwner operation.
     */
    record ListarPorOwnerScenario(
            String descricao,
            UUID tenantId,
            String owner,
            List<ContaPrograma> repositoryResponse,
            int expectedSize,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements TransacaoMilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    // ==================== LISTAR TODOS ====================

    /**
     * Scenario for listarTodos operation.
     */
    record ListarTodosScenario(
            String descricao,
            UUID tenantId,
            List<ContaPrograma> repositoryResponse,
            int expectedSize
    ) implements TransacaoMilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    // ==================== TOTAL MILHAS ====================

    /**
     * Scenario for totalMilhas operation.
     */
    record TotalMilhasScenario(
            String descricao,
            UUID tenantId,
            long repositoryResponse,
            long expectedTotal
    ) implements TransacaoMilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    // ==================== TOTAIS POR OWNER ====================

    /**
     * Scenario for totaisPorOwner operation.
     */
    record TotaisPorOwnerScenario(
            String descricao,
            UUID tenantId,
            Map<String, Long> repositoryResponse,
            int expectedMapSize
    ) implements TransacaoMilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    // ==================== TOTAIS POR PROGRAMA ====================

    /**
     * Scenario for totaisPorPrograma operation.
     */
    record TotaisPorProgramaScenario(
            String descricao,
            UUID tenantId,
            Map<String, Long> repositoryResponse,
            int expectedMapSize
    ) implements TransacaoMilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    // ==================== LISTAR TRANSACOES POR CONTA ====================

    /**
     * Scenario for listarPorContaPrograma operation.
     */
    record ListarTransacoesPorContaScenario(
            String descricao,
            UUID contaProgramaId,
            List<Transacao> repositoryResponse,
            int expectedSize
    ) implements TransacaoMilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    // ==================== LISTAR TRANSACOES POR PERIODO ====================

    /**
     * Scenario for listarPorPeriodo operation.
     */
    record ListarTransacoesPorPeriodoScenario(
            String descricao,
            UUID contaProgramaId,
            LocalDateTime inicio,
            LocalDateTime fim,
            List<Transacao> repositoryResponse,
            int expectedSize,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements TransacaoMilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    // ==================== LISTAR TRANSACOES POR TIPO ====================

    /**
     * Scenario for listarPorTipo operation.
     */
    record ListarTransacoesPorTipoScenario(
            String descricao,
            UUID contaProgramaId,
            TipoTransacao tipo,
            List<Transacao> repositoryResponse,
            int expectedSize
    ) implements TransacaoMilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    // ==================== CONSTRUCTOR VALIDATION ====================

    /**
     * Scenario for constructor validation.
     */
    record ConstructorScenario(
            String descricao,
            boolean contaRepoNull,
            boolean transacaoRepoNull,
            String expectedMessage
    ) implements TransacaoMilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }
}
