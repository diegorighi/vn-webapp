package com.vanessaviagem.backoffice.application.services;

import com.vanessaviagem.backoffice.domain.model.cliente.ClienteDependente;
import com.vanessaviagem.backoffice.domain.model.cliente.ClienteTitular;
import com.vanessaviagem.backoffice.domain.model.Pessoa;
import com.vanessaviagem.backoffice.domain.model.Viagem;
import com.vanessaviagem.backoffice.domain.model.enums.Parentesco;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Sealed interface for all ClienteService test scenarios.
 * Each scenario record encapsulates a complete test case including input,
 * expected output, and optional exception information.
 */
public sealed interface ClienteServiceScenario {

    String descricao();

    // ========== TITULAR SCENARIOS ==========

    record RegistrarTitularScenario(
            String descricao,
            ClienteTitular inputTitular,
            ClienteTitular expectedResult,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements ClienteServiceScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record BuscarTitularScenario(
            String descricao,
            UUID titularId,
            Optional<ClienteTitular> repositoryResult,
            Optional<ClienteTitular> expectedResult
    ) implements ClienteServiceScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record BuscarTodosTitularesScenario(
            String descricao,
            List<ClienteTitular> repositoryResult,
            List<ClienteTitular> expectedResult
    ) implements ClienteServiceScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record AtualizarTitularScenario(
            String descricao,
            ClienteTitular inputTitular,
            Optional<ClienteTitular> existingTitular,
            ClienteTitular expectedResult,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements ClienteServiceScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record DesativarTitularScenario(
            String descricao,
            UUID titularId,
            Optional<ClienteTitular> existingTitular,
            List<ClienteDependente> dependentesAtivos,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements ClienteServiceScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    // ========== DEPENDENTE SCENARIOS ==========

    record AdicionarDependenteScenario(
            String descricao,
            UUID titularId,
            Parentesco parentesco,
            Pessoa dependenteDados,
            Optional<ClienteTitular> titularExistente,
            ClienteDependente expectedResult,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements ClienteServiceScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record BuscarDependenteScenario(
            String descricao,
            UUID dependenteId,
            Optional<ClienteDependente> repositoryResult,
            Optional<ClienteDependente> expectedResult
    ) implements ClienteServiceScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record BuscarDependentesPorTitularScenario(
            String descricao,
            UUID titularId,
            Optional<ClienteTitular> titularExistente,
            List<ClienteDependente> repositoryResult,
            List<ClienteDependente> expectedResult,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements ClienteServiceScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record AtualizarDependenteScenario(
            String descricao,
            ClienteDependente inputDependente,
            Optional<ClienteDependente> existingDependente,
            ClienteDependente expectedResult,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements ClienteServiceScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record RemoverDependenteScenario(
            String descricao,
            UUID dependenteId,
            Optional<ClienteDependente> existingDependente,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements ClienteServiceScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    // ========== VIAGEM SCENARIOS ==========

    record RegistrarViagemScenario(
            String descricao,
            UUID titularId,
            Viagem inputViagem,
            Optional<ClienteTitular> titularExistente,
            Viagem expectedResult,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements ClienteServiceScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record BuscarViagemScenario(
            String descricao,
            UUID viagemId,
            Optional<Viagem> repositoryResult,
            Optional<Viagem> expectedResult
    ) implements ClienteServiceScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record BuscarViagensPorTitularScenario(
            String descricao,
            UUID titularId,
            Optional<ClienteTitular> titularExistente,
            List<Viagem> repositoryResult,
            List<Viagem> expectedResult,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements ClienteServiceScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record AtualizarViagemScenario(
            String descricao,
            Viagem inputViagem,
            Optional<Viagem> existingViagem,
            Viagem expectedResult,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements ClienteServiceScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }

    record CancelarViagemScenario(
            String descricao,
            UUID viagemId,
            Optional<Viagem> existingViagem,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements ClienteServiceScenario {
        @Override
        public String toString() {
            return descricao;
        }
    }
}
