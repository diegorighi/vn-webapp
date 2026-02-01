package com.vanessaviagem.backoffice.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vanessaviagem.backoffice.application.ports.out.ProgramaMilhasRepository;
import com.vanessaviagem.backoffice.application.services.ProgramaMilhasServiceScenarios.AtivarProgramaScenario;
import com.vanessaviagem.backoffice.application.services.ProgramaMilhasServiceScenarios.AtualizarProgramaScenario;
import com.vanessaviagem.backoffice.application.services.ProgramaMilhasServiceScenarios.BuscarProgramaPorBrandScenario;
import com.vanessaviagem.backoffice.application.services.ProgramaMilhasServiceScenarios.BuscarProgramaScenario;
import com.vanessaviagem.backoffice.application.services.ProgramaMilhasServiceScenarios.CadastrarProgramaScenario;
import com.vanessaviagem.backoffice.application.services.ProgramaMilhasServiceScenarios.DesativarProgramaScenario;
import com.vanessaviagem.backoffice.application.services.ProgramaMilhasServiceScenarios.ListarProgramasAtivosScenario;
import com.vanessaviagem.backoffice.application.services.ProgramaMilhasServiceScenarios.ListarProgramasScenario;
import com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhas;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for ProgramaMilhasService CRUD operations.
 * Uses parameterized tests with external data providers as per project conventions.
 */
@ExtendWith(MockitoExtension.class)
class ProgramaMilhasServiceTest {

    private static final String PROVIDER_PATH =
            "com.vanessaviagem.backoffice.application.services.ProgramaMilhasServiceTestDataProvider#";

    @Mock
    private ProgramaMilhasRepository repository;

    private ProgramaMilhasService service;

    @BeforeEach
    void setUp() {
        service = new ProgramaMilhasService(repository);
    }

    // ========================================================================
    // cadastrarPrograma Tests
    // ========================================================================

    @Nested
    @DisplayName("cadastrarPrograma")
    class CadastrarProgramaTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(PROVIDER_PATH + "cadastrarProgramaScenarios")
        void shouldHandleCadastrarPrograma(CadastrarProgramaScenario scenario) {
            // Arrange
            when(repository.existePorBrand(scenario.inputPrograma().brand()))
                    .thenReturn(scenario.brandExiste());

            if (scenario.expectedException() != null) {
                // Act & Assert - Error case
                assertThatThrownBy(() -> service.cadastrarPrograma(scenario.inputPrograma()))
                        .isInstanceOf(scenario.expectedException())
                        .hasMessage(scenario.expectedMessage());

                verify(repository, never()).salvar(any(ProgramaDeMilhas.class));
            } else {
                // Arrange - Success case
                when(repository.salvar(scenario.inputPrograma()))
                        .thenReturn(scenario.expectedResult());

                // Act
                ProgramaDeMilhas result = service.cadastrarPrograma(scenario.inputPrograma());

                // Assert
                assertThat(result).isEqualTo(scenario.expectedResult());
                assertThat(result.id()).isEqualTo(scenario.expectedResult().id());
                assertThat(result.brand()).isEqualTo(scenario.expectedResult().brand());
                assertThat(result.status()).isEqualTo(scenario.expectedResult().status());
                assertThat(result.moeda()).isEqualTo(scenario.expectedResult().moeda());

                verify(repository).existePorBrand(scenario.inputPrograma().brand());
                verify(repository).salvar(scenario.inputPrograma());
            }
        }
    }

    // ========================================================================
    // buscarPrograma Tests
    // ========================================================================

    @Nested
    @DisplayName("buscarPrograma")
    class BuscarProgramaTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(PROVIDER_PATH + "buscarProgramaScenarios")
        void shouldHandleBuscarPrograma(BuscarProgramaScenario scenario) {
            // Arrange
            when(repository.buscarPorId(scenario.programaId()))
                    .thenReturn(scenario.repositoryResult());

            // Act
            Optional<ProgramaDeMilhas> result = service.buscarPrograma(scenario.programaId());

            // Assert
            assertThat(result.isPresent()).isEqualTo(scenario.expectedPresent());

            if (scenario.expectedPresent()) {
                assertThat(result.get()).isEqualTo(scenario.repositoryResult().orElseThrow());
            }

            verify(repository).buscarPorId(scenario.programaId());
        }
    }

    // ========================================================================
    // buscarProgramaPorBrand Tests
    // ========================================================================

    @Nested
    @DisplayName("buscarProgramaPorBrand")
    class BuscarProgramaPorBrandTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(PROVIDER_PATH + "buscarProgramaPorBrandScenarios")
        void shouldHandleBuscarProgramaPorBrand(BuscarProgramaPorBrandScenario scenario) {
            // Arrange
            when(repository.buscarPorBrand(scenario.brand()))
                    .thenReturn(scenario.repositoryResult());

            // Act
            Optional<ProgramaDeMilhas> result = service.buscarProgramaPorBrand(scenario.brand());

            // Assert
            assertThat(result.isPresent()).isEqualTo(scenario.expectedPresent());

            if (scenario.expectedPresent()) {
                assertThat(result.get()).isEqualTo(scenario.repositoryResult().orElseThrow());
                assertThat(result.get().brand()).isEqualTo(scenario.brand());
            }

            verify(repository).buscarPorBrand(scenario.brand());
        }
    }

    // ========================================================================
    // listarProgramas Tests
    // ========================================================================

    @Nested
    @DisplayName("listarProgramas")
    class ListarProgramasTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(PROVIDER_PATH + "listarProgramasScenarios")
        void shouldHandleListarProgramas(ListarProgramasScenario scenario) {
            // Arrange
            when(repository.buscarTodos())
                    .thenReturn(scenario.repositoryResult());

            // Act
            List<ProgramaDeMilhas> result = service.listarProgramas();

            // Assert
            assertThat(result).hasSize(scenario.expectedSize());
            assertThat(result).isEqualTo(scenario.repositoryResult());

            verify(repository).buscarTodos();
        }
    }

    // ========================================================================
    // listarProgramasAtivos Tests
    // ========================================================================

    @Nested
    @DisplayName("listarProgramasAtivos")
    class ListarProgramasAtivosTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(PROVIDER_PATH + "listarProgramasAtivosScenarios")
        void shouldHandleListarProgramasAtivos(ListarProgramasAtivosScenario scenario) {
            // Arrange
            when(repository.buscarAtivos())
                    .thenReturn(scenario.repositoryResult());

            // Act
            List<ProgramaDeMilhas> result = service.listarProgramasAtivos();

            // Assert
            assertThat(result).hasSize(scenario.expectedSize());
            assertThat(result).isEqualTo(scenario.repositoryResult());
            assertThat(result).allMatch(ProgramaDeMilhas::isAtivo);

            verify(repository).buscarAtivos();
        }
    }

    // ========================================================================
    // atualizarPrograma Tests
    // ========================================================================

    @Nested
    @DisplayName("atualizarPrograma")
    class AtualizarProgramaTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(PROVIDER_PATH + "atualizarProgramaScenarios")
        void shouldHandleAtualizarPrograma(AtualizarProgramaScenario scenario) {
            // Arrange
            when(repository.buscarPorId(scenario.inputPrograma().id()))
                    .thenReturn(scenario.existingPrograma());

            if (scenario.expectedException() != null) {
                // Act & Assert - Error case
                assertThatThrownBy(() -> service.atualizarPrograma(scenario.inputPrograma()))
                        .isInstanceOf(scenario.expectedException())
                        .hasMessage(scenario.expectedMessage());

                verify(repository, never()).atualizar(any(ProgramaDeMilhas.class));
            } else {
                // Arrange - Success case
                when(repository.atualizar(scenario.inputPrograma()))
                        .thenReturn(scenario.expectedResult());

                // Act
                ProgramaDeMilhas result = service.atualizarPrograma(scenario.inputPrograma());

                // Assert
                assertThat(result).isEqualTo(scenario.expectedResult());
                assertThat(result.id()).isEqualTo(scenario.inputPrograma().id());

                verify(repository).buscarPorId(scenario.inputPrograma().id());
                verify(repository).atualizar(scenario.inputPrograma());
            }
        }
    }

    // ========================================================================
    // desativarPrograma Tests
    // ========================================================================

    @Nested
    @DisplayName("desativarPrograma")
    class DesativarProgramaTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(PROVIDER_PATH + "desativarProgramaScenarios")
        void shouldHandleDesativarPrograma(DesativarProgramaScenario scenario) {
            // Arrange
            when(repository.buscarPorId(scenario.programaId()))
                    .thenReturn(scenario.existingPrograma());

            if (scenario.expectedException() != null) {
                // Act & Assert - Error case
                assertThatThrownBy(() -> service.desativarPrograma(scenario.programaId()))
                        .isInstanceOf(scenario.expectedException())
                        .hasMessage(scenario.expectedMessage());

                verify(repository, never()).atualizar(any(ProgramaDeMilhas.class));
            } else {
                // Arrange - Success case
                ProgramaDeMilhas programaDesativado = scenario.existingPrograma()
                        .orElseThrow()
                        .desativar();
                when(repository.atualizar(programaDesativado))
                        .thenReturn(programaDesativado);

                // Act
                service.desativarPrograma(scenario.programaId());

                // Assert
                verify(repository).buscarPorId(scenario.programaId());
                verify(repository).atualizar(programaDesativado);
            }
        }
    }

    // ========================================================================
    // ativarPrograma Tests
    // ========================================================================

    @Nested
    @DisplayName("ativarPrograma")
    class AtivarProgramaTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(PROVIDER_PATH + "ativarProgramaScenarios")
        void shouldHandleAtivarPrograma(AtivarProgramaScenario scenario) {
            // Arrange
            when(repository.buscarPorId(scenario.programaId()))
                    .thenReturn(scenario.existingPrograma());

            if (scenario.expectedException() != null) {
                // Act & Assert - Error case
                assertThatThrownBy(() -> service.ativarPrograma(scenario.programaId()))
                        .isInstanceOf(scenario.expectedException())
                        .hasMessage(scenario.expectedMessage());

                verify(repository, never()).atualizar(any(ProgramaDeMilhas.class));
            } else {
                // Arrange - Success case
                ProgramaDeMilhas programaAtivado = scenario.existingPrograma()
                        .orElseThrow()
                        .ativar();
                when(repository.atualizar(programaAtivado))
                        .thenReturn(programaAtivado);

                // Act
                service.ativarPrograma(scenario.programaId());

                // Assert
                verify(repository).buscarPorId(scenario.programaId());
                verify(repository).atualizar(programaAtivado);
            }
        }
    }
}
