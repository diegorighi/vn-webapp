package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.StatusPrograma;
import java.util.UUID;

/**
 * Scenarios for testing ProgramaDeMilhas domain entity.
 */
public sealed interface ProgramaDeMilhasScenario {

    String descricao();

    record CriacaoValidaScenario(
            String descricao,
            UUID id,
            String brand,
            StatusPrograma status,
            String moeda,
            ConfigArredondamento regrasArredondamento
    ) implements ProgramaDeMilhasScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record CriacaoInvalidaScenario(
            String descricao,
            UUID id,
            String brand,
            StatusPrograma status,
            String moeda,
            ConfigArredondamento regrasArredondamento,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements ProgramaDeMilhasScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record FactoryCriarScenario(
            String descricao,
            UUID id,
            String brand,
            StatusPrograma expectedStatus,
            String expectedMoeda
    ) implements ProgramaDeMilhasScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record FactoryCriarComMoedaScenario(
            String descricao,
            UUID id,
            String brand,
            String moeda,
            StatusPrograma expectedStatus
    ) implements ProgramaDeMilhasScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record IsAtivoScenario(
            String descricao,
            ProgramaDeMilhas programa,
            boolean expectedAtivo
    ) implements ProgramaDeMilhasScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record DesativarScenario(
            String descricao,
            ProgramaDeMilhas programaOriginal,
            StatusPrograma expectedStatus
    ) implements ProgramaDeMilhasScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record AtivarScenario(
            String descricao,
            ProgramaDeMilhas programaOriginal,
            StatusPrograma expectedStatus
    ) implements ProgramaDeMilhasScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record ComRegrasArredondamentoScenario(
            String descricao,
            ProgramaDeMilhas programaOriginal,
            ConfigArredondamento novaConfig
    ) implements ProgramaDeMilhasScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }
}
