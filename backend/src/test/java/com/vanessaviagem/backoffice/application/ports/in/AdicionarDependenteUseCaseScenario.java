package com.vanessaviagem.backoffice.application.ports.in;

import com.vanessaviagem.backoffice.domain.model.cliente.ClienteDependente;
import com.vanessaviagem.backoffice.domain.model.Pessoa;
import com.vanessaviagem.backoffice.domain.model.enums.Parentesco;
import java.util.UUID;

public sealed interface AdicionarDependenteUseCaseScenario {

    String descricao();

    record CommandValidScenario(
            String descricao,
            UUID titularId,
            Parentesco parentesco,
            Pessoa dadosPessoais
    ) implements AdicionarDependenteUseCaseScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record CommandInvalidScenario(
            String descricao,
            UUID titularId,
            Parentesco parentesco,
            Pessoa dadosPessoais,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements AdicionarDependenteUseCaseScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record ResultValidScenario(
            String descricao,
            UUID dependenteId,
            ClienteDependente dependente
    ) implements AdicionarDependenteUseCaseScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record ResultInvalidScenario(
            String descricao,
            UUID dependenteId,
            ClienteDependente dependente,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements AdicionarDependenteUseCaseScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }
}
