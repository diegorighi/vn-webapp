package com.vanessaviagem.backoffice.application.ports.in;

import com.vanessaviagem.backoffice.domain.model.Viagem;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public sealed interface RegistrarViagemUseCaseScenario {

    String descricao();

    record CommandValidScenario(
            String descricao,
            UUID clienteId,
            String localizador,
            List<String> trecho,
            OffsetDateTime data,
            String assento,
            List<String> companhiaAereaList,
            String moeda,
            BigDecimal precoTotal
    ) implements RegistrarViagemUseCaseScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record CommandInvalidScenario(
            String descricao,
            UUID clienteId,
            String localizador,
            List<String> trecho,
            OffsetDateTime data,
            String assento,
            List<String> companhiaAereaList,
            String moeda,
            BigDecimal precoTotal,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements RegistrarViagemUseCaseScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record ResultValidScenario(
            String descricao,
            UUID viagemId,
            Viagem viagem
    ) implements RegistrarViagemUseCaseScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record ResultInvalidScenario(
            String descricao,
            UUID viagemId,
            Viagem viagem,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements RegistrarViagemUseCaseScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }
}
