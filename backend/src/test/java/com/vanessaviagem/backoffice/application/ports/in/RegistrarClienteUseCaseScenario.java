package com.vanessaviagem.backoffice.application.ports.in;

import com.vanessaviagem.backoffice.domain.model.cliente.ClienteTitular;
import com.vanessaviagem.backoffice.domain.model.Pessoa;
import java.util.UUID;

public sealed interface RegistrarClienteUseCaseScenario {

    String descricao();

    record CommandValidScenario(
            String descricao,
            Pessoa dadosPessoais
    ) implements RegistrarClienteUseCaseScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record CommandInvalidScenario(
            String descricao,
            Pessoa dadosPessoais,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements RegistrarClienteUseCaseScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record ResultValidScenario(
            String descricao,
            UUID clienteId,
            ClienteTitular cliente
    ) implements RegistrarClienteUseCaseScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record ResultInvalidScenario(
            String descricao,
            UUID clienteId,
            ClienteTitular cliente,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements RegistrarClienteUseCaseScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }
}
