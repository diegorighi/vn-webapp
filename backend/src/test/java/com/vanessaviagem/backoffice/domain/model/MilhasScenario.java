package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas;
import java.math.BigDecimal;
import java.util.UUID;

public sealed interface MilhasScenario {

    String descricao();

    record PrecoMedioScenario(
            String descricao,
            int quantidade,
            BigDecimal valor,
            BigDecimal esperado
    ) implements MilhasScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record CriacaoValidaScenario(
            String descricao,
            TipoProgramaMilhas programa,
            int quantidade,
            BigDecimal valor
    ) implements MilhasScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record CriacaoInvalidaScenario(
            String descricao,
            TipoProgramaMilhas programa,
            int quantidade,
            BigDecimal valor,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements MilhasScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record ComIdFactoryScenario(
            String descricao,
            UUID id,
            TipoProgramaMilhas programa,
            int quantidade,
            BigDecimal valor
    ) implements MilhasScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record ComIdFactoryInvalidaScenario(
            String descricao,
            UUID id,
            TipoProgramaMilhas programa,
            int quantidade,
            BigDecimal valor,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements MilhasScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record ComQuantidadeScenario(
            String descricao,
            Milhas milhasOriginal,
            int novaQuantidade
    ) implements MilhasScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record ComValorScenario(
            String descricao,
            Milhas milhasOriginal,
            BigDecimal novoValor
    ) implements MilhasScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record ComIdInstanceScenario(
            String descricao,
            Milhas milhasOriginal,
            UUID novoId
    ) implements MilhasScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }
}
