package com.vanessaviagem.backoffice.application.services;

import com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhas;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Scenario records for ProgramaMilhasService parameterized tests.
 * Each scenario encapsulates all data needed for a specific test case.
 */
public sealed interface ProgramaMilhasServiceScenarios {

    /**
     * Returns the scenario description for test naming.
     *
     * @return the description
     */
    String descricao();

    /**
     * Scenario for testing cadastrarPrograma operation.
     *
     * @param descricao scenario description
     * @param inputPrograma the program to register
     * @param brandExiste whether the brand already exists in repository
     * @param expectedResult the expected program after registration
     * @param expectedException expected exception class if error scenario
     * @param expectedMessage expected exception message if error scenario
     */
    record CadastrarProgramaScenario(
            String descricao,
            ProgramaDeMilhas inputPrograma,
            boolean brandExiste,
            ProgramaDeMilhas expectedResult,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements ProgramaMilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for testing buscarPrograma operation.
     *
     * @param descricao scenario description
     * @param programaId the ID to search for
     * @param repositoryResult what the repository will return
     * @param expectedPresent whether result should be present
     */
    record BuscarProgramaScenario(
            String descricao,
            UUID programaId,
            Optional<ProgramaDeMilhas> repositoryResult,
            boolean expectedPresent
    ) implements ProgramaMilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for testing buscarProgramaPorBrand operation.
     *
     * @param descricao scenario description
     * @param brand the brand to search for
     * @param repositoryResult what the repository will return
     * @param expectedPresent whether result should be present
     */
    record BuscarProgramaPorBrandScenario(
            String descricao,
            String brand,
            Optional<ProgramaDeMilhas> repositoryResult,
            boolean expectedPresent
    ) implements ProgramaMilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for testing listarProgramas operation.
     *
     * @param descricao scenario description
     * @param repositoryResult the list returned by repository
     * @param expectedSize expected size of result list
     */
    record ListarProgramasScenario(
            String descricao,
            List<ProgramaDeMilhas> repositoryResult,
            int expectedSize
    ) implements ProgramaMilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for testing listarProgramasAtivos operation.
     *
     * @param descricao scenario description
     * @param repositoryResult the list returned by repository
     * @param expectedSize expected size of result list
     */
    record ListarProgramasAtivosScenario(
            String descricao,
            List<ProgramaDeMilhas> repositoryResult,
            int expectedSize
    ) implements ProgramaMilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for testing atualizarPrograma operation.
     *
     * @param descricao scenario description
     * @param inputPrograma the program with updated data
     * @param existingPrograma Optional of existing program in repository
     * @param expectedResult the expected program after update
     * @param expectedException expected exception class if error scenario
     * @param expectedMessage expected exception message if error scenario
     */
    record AtualizarProgramaScenario(
            String descricao,
            ProgramaDeMilhas inputPrograma,
            Optional<ProgramaDeMilhas> existingPrograma,
            ProgramaDeMilhas expectedResult,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements ProgramaMilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for testing desativarPrograma operation.
     *
     * @param descricao scenario description
     * @param programaId the ID of program to deactivate
     * @param existingPrograma Optional of existing program in repository
     * @param expectedException expected exception class if error scenario
     * @param expectedMessage expected exception message if error scenario
     */
    record DesativarProgramaScenario(
            String descricao,
            UUID programaId,
            Optional<ProgramaDeMilhas> existingPrograma,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements ProgramaMilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }

    /**
     * Scenario for testing ativarPrograma operation.
     *
     * @param descricao scenario description
     * @param programaId the ID of program to activate
     * @param existingPrograma Optional of existing program in repository
     * @param expectedException expected exception class if error scenario
     * @param expectedMessage expected exception message if error scenario
     */
    record AtivarProgramaScenario(
            String descricao,
            UUID programaId,
            Optional<ProgramaDeMilhas> existingPrograma,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) implements ProgramaMilhasServiceScenarios {
        @Override
        public String toString() {
            return descricao;
        }
    }
}
