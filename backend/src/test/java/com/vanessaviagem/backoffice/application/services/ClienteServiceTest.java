package com.vanessaviagem.backoffice.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vanessaviagem.backoffice.application.ports.out.ClienteRepository;
import com.vanessaviagem.backoffice.application.ports.out.ViagemRepository;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.AdicionarDependenteScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.AtualizarDependenteScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.AtualizarTitularScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.AtualizarViagemScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.BuscarDependenteScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.BuscarDependentesPorTitularScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.BuscarTitularScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.BuscarTodosTitularesScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.BuscarViagemScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.BuscarViagensPorTitularScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.CancelarViagemScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.DesativarTitularScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.RegistrarTitularScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.RegistrarViagemScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.RemoverDependenteScenario;
import com.vanessaviagem.backoffice.domain.model.cliente.ClienteDependente;
import com.vanessaviagem.backoffice.domain.model.cliente.ClienteTitular;
import com.vanessaviagem.backoffice.domain.model.TenantContext;
import com.vanessaviagem.backoffice.domain.model.Viagem;
import com.vanessaviagem.backoffice.domain.model.enums.RoleType;
import com.vanessaviagem.backoffice.domain.model.enums.ViagemStatus;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Comprehensive unit tests for ClienteService CRUD operations.
 * All tests use @ParameterizedTest with external data providers.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteService")
class ClienteServiceTest {

    private static final String DATA_PROVIDER_PATH =
            "com.vanessaviagem.backoffice.application.services.ClienteServiceTestDataProvider#";

    private static final UUID TEST_TENANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID TEST_USER_ID = UUID.fromString("20000000-0000-0000-0000-000000000002");

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ViagemRepository viagemRepository;

    @Captor
    private ArgumentCaptor<ClienteTitular> titularCaptor;

    @Captor
    private ArgumentCaptor<Viagem> viagemCaptor;

    @Captor
    private ArgumentCaptor<ClienteDependente> dependenteCaptor;

    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        TenantContext.set(
                TEST_TENANT_ID,
                TEST_USER_ID,
                "admin@test.com",
                Set.of("*"),
                RoleType.ADMIN,
                true
        );
        clienteService = new ClienteService(clienteRepository, viagemRepository);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    // ========== TITULAR OPERATIONS ==========

    @Nested
    @DisplayName("Registrar Titular")
    class RegistrarTitularTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PATH + "registrarTitularScenarios")
        @DisplayName("should handle titular registration")
        void shouldRegistrarTitular(RegistrarTitularScenario scenario) {
            when(clienteRepository.salvarTitular(scenario.inputTitular()))
                    .thenReturn(scenario.expectedResult());

            ClienteTitular result = clienteService.registrarTitular(scenario.inputTitular());

            assertThat(result).isEqualTo(scenario.expectedResult());
            verify(clienteRepository).salvarTitular(scenario.inputTitular());
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PATH + "registrarTitularNullScenarios")
        @DisplayName("should handle null input for titular registration")
        void shouldThrowExceptionWhenTitularIsNull(RegistrarTitularScenario scenario) {
            assertThatThrownBy(() -> clienteService.registrarTitular(scenario.inputTitular()))
                    .isInstanceOf(scenario.expectedException())
                    .hasMessage(scenario.expectedMessage());

            verify(clienteRepository, never()).salvarTitular(any());
        }
    }

    @Nested
    @DisplayName("Buscar Titular")
    class BuscarTitularTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PATH + "buscarTitularScenarios")
        @DisplayName("should handle titular search by ID")
        void shouldBuscarTitular(BuscarTitularScenario scenario) {
            when(clienteRepository.buscarTitular(scenario.titularId()))
                    .thenReturn(scenario.repositoryResult());

            Optional<ClienteTitular> result = clienteService.buscarTitular(scenario.titularId());

            assertThat(result).isEqualTo(scenario.expectedResult());
            verify(clienteRepository).buscarTitular(scenario.titularId());
        }
    }

    @Nested
    @DisplayName("Buscar Todos Titulares")
    class BuscarTodosTitularesTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PATH + "buscarTodosTitularesScenarios")
        @DisplayName("should handle list all titulares")
        void shouldBuscarTodosTitulares(BuscarTodosTitularesScenario scenario) {
            when(clienteRepository.buscarTodosTitulares())
                    .thenReturn(scenario.repositoryResult());

            List<ClienteTitular> result = clienteService.buscarTodosTitulares();

            assertThat(result).isEqualTo(scenario.expectedResult());
            assertThat(result).hasSize(scenario.expectedResult().size());
            verify(clienteRepository).buscarTodosTitulares();
        }
    }

    @Nested
    @DisplayName("Atualizar Titular")
    class AtualizarTitularTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PATH + "atualizarTitularScenarios")
        @DisplayName("should handle titular update")
        void shouldAtualizarTitular(AtualizarTitularScenario scenario) {
            when(clienteRepository.buscarTitular(scenario.inputTitular().clienteId()))
                    .thenReturn(scenario.existingTitular());

            if (scenario.expectedException() != null) {
                assertThatThrownBy(() -> clienteService.atualizarTitular(scenario.inputTitular()))
                        .isInstanceOf(scenario.expectedException())
                        .hasMessage(scenario.expectedMessage());

                verify(clienteRepository, never()).atualizarTitular(any());
            } else {
                when(clienteRepository.atualizarTitular(scenario.inputTitular()))
                        .thenReturn(scenario.expectedResult());

                ClienteTitular result = clienteService.atualizarTitular(scenario.inputTitular());

                assertThat(result).isEqualTo(scenario.expectedResult());
                verify(clienteRepository).atualizarTitular(scenario.inputTitular());
            }
        }
    }

    @Nested
    @DisplayName("Desativar Titular")
    class DesativarTitularTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PATH + "desativarTitularScenarios")
        @DisplayName("should handle titular deactivation")
        void shouldDesativarTitular(DesativarTitularScenario scenario) {
            when(clienteRepository.buscarTitular(scenario.titularId()))
                    .thenReturn(scenario.existingTitular());

            if (scenario.expectedException() != null) {
                assertThatThrownBy(() -> clienteService.desativarTitular(scenario.titularId()))
                        .isInstanceOf(scenario.expectedException())
                        .hasMessage(scenario.expectedMessage());

                verify(clienteRepository, never()).atualizarTitular(any());
            } else {
                when(clienteRepository.buscarDependentesPorTitular(scenario.titularId()))
                        .thenReturn(scenario.dependentesAtivos());

                clienteService.desativarTitular(scenario.titularId());

                verify(clienteRepository).atualizarTitular(titularCaptor.capture());
                ClienteTitular capturedTitular = titularCaptor.getValue();

                assertThat(capturedTitular.ativo()).isFalse();
                assertThat(capturedTitular.clienteId()).isEqualTo(scenario.titularId());
            }
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PATH + "desativarTitularComDependentesScenarios")
        @DisplayName("should propagate deactivation to dependentes")
        void shouldPropagateDeactivationToDependentes(DesativarTitularScenario scenario) {
            when(clienteRepository.buscarTitular(scenario.titularId()))
                    .thenReturn(scenario.existingTitular());
            when(clienteRepository.buscarDependentesPorTitular(scenario.titularId()))
                    .thenReturn(scenario.dependentesAtivos());
            when(clienteRepository.atualizarDependente(any(ClienteDependente.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            clienteService.desativarTitular(scenario.titularId());

            // Verify titular was deactivated
            verify(clienteRepository).atualizarTitular(titularCaptor.capture());
            assertThat(titularCaptor.getValue().ativo()).isFalse();

            // Verify all dependentes were deactivated
            verify(clienteRepository, org.mockito.Mockito.times(scenario.dependentesAtivos().size()))
                    .atualizarDependente(dependenteCaptor.capture());

            List<ClienteDependente> capturedDependentes = dependenteCaptor.getAllValues();
            assertThat(capturedDependentes).hasSize(scenario.dependentesAtivos().size());
            capturedDependentes.forEach(dep -> assertThat(dep.ativo()).isFalse());
        }
    }

    // ========== DEPENDENTE OPERATIONS ==========

    @Nested
    @DisplayName("Adicionar Dependente")
    class AdicionarDependenteTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PATH + "adicionarDependenteScenarios")
        @DisplayName("should handle dependente addition")
        void shouldAdicionarDependente(AdicionarDependenteScenario scenario) {
            when(clienteRepository.buscarTitular(scenario.titularId()))
                    .thenReturn(scenario.titularExistente());

            if (scenario.expectedException() != null) {
                assertThatThrownBy(() ->
                        clienteService.adicionarDependente(
                                scenario.titularId(),
                                scenario.parentesco(),
                                scenario.dependenteDados()
                        )
                )
                        .isInstanceOf(scenario.expectedException())
                        .hasMessage(scenario.expectedMessage());

                verify(clienteRepository, never()).salvarDependente(any());
            } else {
                when(clienteRepository.salvarDependente(any(ClienteDependente.class)))
                        .thenReturn(scenario.expectedResult());

                ClienteDependente result = clienteService.adicionarDependente(
                        scenario.titularId(),
                        scenario.parentesco(),
                        scenario.dependenteDados()
                );

                assertThat(result.dadosPessoais()).isEqualTo(scenario.dependenteDados());
                assertThat(result.parentesco()).isEqualTo(scenario.parentesco());
                assertThat(result.ativo()).isTrue();
                verify(clienteRepository).salvarDependente(any(ClienteDependente.class));
            }
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PATH + "adicionarDependenteTitularInativoScenarios")
        @DisplayName("should throw TitularInativoException when titular is inactive")
        void shouldThrowWhenTitularIsInactive(AdicionarDependenteScenario scenario) {
            when(clienteRepository.buscarTitular(scenario.titularId()))
                    .thenReturn(scenario.titularExistente());

            assertThatThrownBy(() ->
                    clienteService.adicionarDependente(
                            scenario.titularId(),
                            scenario.parentesco(),
                            scenario.dependenteDados()
                    )
            )
                    .isInstanceOf(scenario.expectedException())
                    .hasMessage(scenario.expectedMessage());

            verify(clienteRepository, never()).salvarDependente(any());
        }
    }

    @Nested
    @DisplayName("Buscar Dependente")
    class BuscarDependenteTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PATH + "buscarDependenteScenarios")
        @DisplayName("should handle dependente search by ID")
        void shouldBuscarDependente(BuscarDependenteScenario scenario) {
            when(clienteRepository.buscarDependente(scenario.dependenteId()))
                    .thenReturn(scenario.repositoryResult());

            Optional<ClienteDependente> result = clienteService.buscarDependente(scenario.dependenteId());

            assertThat(result).isEqualTo(scenario.expectedResult());
            verify(clienteRepository).buscarDependente(scenario.dependenteId());
        }
    }

    @Nested
    @DisplayName("Buscar Dependentes Por Titular")
    class BuscarDependentesPorTitularTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PATH + "buscarDependentesPorTitularScenarios")
        @DisplayName("should handle list dependentes by titular")
        void shouldBuscarDependentesPorTitular(BuscarDependentesPorTitularScenario scenario) {
            when(clienteRepository.buscarTitular(scenario.titularId()))
                    .thenReturn(scenario.titularExistente());

            if (scenario.expectedException() != null) {
                assertThatThrownBy(() ->
                        clienteService.buscarDependentesPorTitular(scenario.titularId())
                )
                        .isInstanceOf(scenario.expectedException())
                        .hasMessage(scenario.expectedMessage());

                verify(clienteRepository, never()).buscarDependentesPorTitular(any());
            } else {
                when(clienteRepository.buscarDependentesPorTitular(scenario.titularId()))
                        .thenReturn(scenario.repositoryResult());

                List<ClienteDependente> result = clienteService.buscarDependentesPorTitular(
                        scenario.titularId()
                );

                assertThat(result).isEqualTo(scenario.expectedResult());
                assertThat(result).hasSize(scenario.expectedResult().size());
                verify(clienteRepository).buscarDependentesPorTitular(scenario.titularId());
            }
        }
    }

    @Nested
    @DisplayName("Atualizar Dependente")
    class AtualizarDependenteTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PATH + "atualizarDependenteScenarios")
        @DisplayName("should handle dependente update")
        void shouldAtualizarDependente(AtualizarDependenteScenario scenario) {
            when(clienteRepository.buscarDependente(scenario.inputDependente().clienteId()))
                    .thenReturn(scenario.existingDependente());

            if (scenario.expectedException() != null) {
                assertThatThrownBy(() ->
                        clienteService.atualizarDependente(scenario.inputDependente())
                )
                        .isInstanceOf(scenario.expectedException())
                        .hasMessage(scenario.expectedMessage());

                verify(clienteRepository, never()).atualizarDependente(any());
            } else {
                when(clienteRepository.atualizarDependente(scenario.inputDependente()))
                        .thenReturn(scenario.expectedResult());

                ClienteDependente result = clienteService.atualizarDependente(scenario.inputDependente());

                assertThat(result).isEqualTo(scenario.expectedResult());
                verify(clienteRepository).atualizarDependente(scenario.inputDependente());
            }
        }
    }

    @Nested
    @DisplayName("Remover Dependente")
    class RemoverDependenteTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PATH + "removerDependenteScenarios")
        @DisplayName("should handle dependente removal")
        void shouldRemoverDependente(RemoverDependenteScenario scenario) {
            when(clienteRepository.buscarDependente(scenario.dependenteId()))
                    .thenReturn(scenario.existingDependente());

            if (scenario.expectedException() != null) {
                assertThatThrownBy(() -> clienteService.removerDependente(scenario.dependenteId()))
                        .isInstanceOf(scenario.expectedException())
                        .hasMessage(scenario.expectedMessage());

                verify(clienteRepository, never()).excluirDependente(any());
            } else {
                clienteService.removerDependente(scenario.dependenteId());

                verify(clienteRepository).excluirDependente(scenario.dependenteId());
            }
        }
    }

    // ========== VIAGEM OPERATIONS ==========

    @Nested
    @DisplayName("Registrar Viagem")
    class RegistrarViagemTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PATH + "registrarViagemScenarios")
        @DisplayName("should handle viagem registration")
        void shouldRegistrarViagem(RegistrarViagemScenario scenario) {
            when(clienteRepository.buscarTitular(scenario.titularId()))
                    .thenReturn(scenario.titularExistente());

            if (scenario.expectedException() != null) {
                assertThatThrownBy(() ->
                        clienteService.registrarViagem(scenario.titularId(), scenario.inputViagem())
                )
                        .isInstanceOf(scenario.expectedException())
                        .hasMessage(scenario.expectedMessage());

                verify(viagemRepository, never()).salvar(any(), any(), any());
            } else {
                when(viagemRepository.salvar(eq(TEST_TENANT_ID), eq(scenario.titularId()), eq(scenario.inputViagem())))
                        .thenReturn(scenario.expectedResult());

                Viagem result = clienteService.registrarViagem(
                        scenario.titularId(),
                        scenario.inputViagem()
                );

                assertThat(result).isEqualTo(scenario.expectedResult());
                verify(viagemRepository).salvar(TEST_TENANT_ID, scenario.titularId(), scenario.inputViagem());
            }
        }
    }

    @Nested
    @DisplayName("Buscar Viagem")
    class BuscarViagemTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PATH + "buscarViagemScenarios")
        @DisplayName("should handle viagem search by ID")
        void shouldBuscarViagem(BuscarViagemScenario scenario) {
            when(viagemRepository.buscarPorId(TEST_TENANT_ID, scenario.viagemId()))
                    .thenReturn(scenario.repositoryResult());

            Optional<Viagem> result = clienteService.buscarViagem(scenario.viagemId());

            assertThat(result).isEqualTo(scenario.expectedResult());
            verify(viagemRepository).buscarPorId(TEST_TENANT_ID, scenario.viagemId());
        }
    }

    @Nested
    @DisplayName("Buscar Viagens Por Titular")
    class BuscarViagensPorTitularTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PATH + "buscarViagensPorTitularScenarios")
        @DisplayName("should handle list viagens by titular")
        void shouldBuscarViagensPorTitular(BuscarViagensPorTitularScenario scenario) {
            when(clienteRepository.buscarTitular(scenario.titularId()))
                    .thenReturn(scenario.titularExistente());

            if (scenario.expectedException() != null) {
                assertThatThrownBy(() ->
                        clienteService.buscarViagensPorTitular(scenario.titularId())
                )
                        .isInstanceOf(scenario.expectedException())
                        .hasMessage(scenario.expectedMessage());

                verify(viagemRepository, never()).buscarPorTitular(any(), any());
            } else {
                when(viagemRepository.buscarPorTitular(TEST_TENANT_ID, scenario.titularId()))
                        .thenReturn(scenario.repositoryResult());

                List<Viagem> result = clienteService.buscarViagensPorTitular(scenario.titularId());

                assertThat(result).isEqualTo(scenario.expectedResult());
                assertThat(result).hasSize(scenario.expectedResult().size());
                verify(viagemRepository).buscarPorTitular(TEST_TENANT_ID, scenario.titularId());
            }
        }
    }

    @Nested
    @DisplayName("Atualizar Viagem")
    class AtualizarViagemTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PATH + "atualizarViagemScenarios")
        @DisplayName("should handle viagem update")
        void shouldAtualizarViagem(AtualizarViagemScenario scenario) {
            when(viagemRepository.buscarPorId(TEST_TENANT_ID, scenario.inputViagem().viagemId()))
                    .thenReturn(scenario.existingViagem());

            if (scenario.expectedException() != null) {
                assertThatThrownBy(() -> clienteService.atualizarViagem(scenario.inputViagem()))
                        .isInstanceOf(scenario.expectedException())
                        .hasMessage(scenario.expectedMessage());

                verify(viagemRepository, never()).atualizar(any(), any());
            } else {
                when(viagemRepository.atualizar(TEST_TENANT_ID, scenario.inputViagem()))
                        .thenReturn(scenario.expectedResult());

                Viagem result = clienteService.atualizarViagem(scenario.inputViagem());

                assertThat(result).isEqualTo(scenario.expectedResult());
                verify(viagemRepository).atualizar(TEST_TENANT_ID, scenario.inputViagem());
            }
        }
    }

    @Nested
    @DisplayName("Cancelar Viagem")
    class CancelarViagemTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PATH + "cancelarViagemScenarios")
        @DisplayName("should handle viagem cancellation")
        void shouldCancelarViagem(CancelarViagemScenario scenario) {
            when(viagemRepository.buscarPorId(TEST_TENANT_ID, scenario.viagemId()))
                    .thenReturn(scenario.existingViagem());

            if (scenario.expectedException() != null) {
                assertThatThrownBy(() -> clienteService.cancelarViagem(scenario.viagemId()))
                        .isInstanceOf(scenario.expectedException())
                        .hasMessage(scenario.expectedMessage());

                verify(viagemRepository, never()).atualizar(any(), any());
            } else {
                clienteService.cancelarViagem(scenario.viagemId());

                verify(viagemRepository).atualizar(eq(TEST_TENANT_ID), viagemCaptor.capture());
                Viagem capturedViagem = viagemCaptor.getValue();

                assertThat(capturedViagem.status()).isEqualTo(ViagemStatus.CANCELADO);
                assertThat(capturedViagem.viagemId()).isEqualTo(scenario.viagemId());
                assertThat(capturedViagem.updatedAt()).isNotNull();
            }
        }
    }
}
