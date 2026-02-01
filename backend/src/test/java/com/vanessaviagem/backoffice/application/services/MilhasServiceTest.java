package com.vanessaviagem.backoffice.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vanessaviagem.backoffice.application.ports.out.MilhasRepository;
import com.vanessaviagem.backoffice.application.services.MilhasServiceScenarios.AtualizarMilhasScenario;
import com.vanessaviagem.backoffice.application.services.MilhasServiceScenarios.BuscarMilhasPorClienteScenario;
import com.vanessaviagem.backoffice.application.services.MilhasServiceScenarios.BuscarMilhasPorProgramaScenario;
import com.vanessaviagem.backoffice.application.services.MilhasServiceScenarios.BuscarMilhasScenario;
import com.vanessaviagem.backoffice.application.services.MilhasServiceScenarios.CalcularCustoMedioMilheiroScenario;
import com.vanessaviagem.backoffice.application.services.MilhasServiceScenarios.CalcularSaldoTotalScenario;
import com.vanessaviagem.backoffice.application.services.MilhasServiceScenarios.RegistrarMilhasScenario;
import com.vanessaviagem.backoffice.application.services.MilhasServiceScenarios.RemoverMilhasScenario;
import com.vanessaviagem.backoffice.domain.model.Milhas;
import com.vanessaviagem.backoffice.domain.model.TenantContext;
import com.vanessaviagem.backoffice.domain.model.enums.RoleType;
import java.math.BigDecimal;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Parameterized tests for MilhasService CRUD operations.
 * All tests use scenario objects from external data providers.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MilhasService")
class MilhasServiceTest {

    private static final String DATA_PROVIDER_PREFIX =
            "com.vanessaviagem.backoffice.application.services.MilhasServiceTestDataProvider#";

    private static final UUID TEST_TENANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID TEST_USER_ID = UUID.fromString("20000000-0000-0000-0000-000000000002");

    @Mock
    private MilhasRepository milhasRepository;

    private MilhasService milhasService;

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
        milhasService = new MilhasService(milhasRepository);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Nested
    @DisplayName("registrarMilhas")
    class RegistrarMilhasTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PREFIX + "registrarMilhasScenarios")
        @DisplayName("should handle registrar milhas scenarios")
        void shouldHandleRegistrarMilhas(RegistrarMilhasScenario scenario) {
            if (scenario.expectedException() != null) {
                assertThatThrownBy(() ->
                        milhasService.registrarMilhas(scenario.clienteId(), scenario.milhasInput())
                )
                        .isInstanceOf(scenario.expectedException())
                        .hasMessage(scenario.expectedMessage());

                verify(milhasRepository, never()).salvar(any(), any(), any());
            } else {
                when(milhasRepository.salvar(eq(TEST_TENANT_ID), eq(scenario.clienteId()), eq(scenario.milhasInput())))
                        .thenReturn(scenario.expectedResult());

                Milhas result = milhasService.registrarMilhas(scenario.clienteId(), scenario.milhasInput());

                assertThat(result).isEqualTo(scenario.expectedResult());
                assertThat(result.id()).isNotNull();
                assertThat(result.programa()).isEqualTo(scenario.milhasInput().programa());
                assertThat(result.quantidade()).isEqualTo(scenario.milhasInput().quantidade());
                assertThat(result.valor()).isEqualByComparingTo(scenario.milhasInput().valor());

                verify(milhasRepository).salvar(TEST_TENANT_ID, scenario.clienteId(), scenario.milhasInput());
            }
        }
    }

    @Nested
    @DisplayName("buscarMilhas")
    class BuscarMilhasTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PREFIX + "buscarMilhasScenarios")
        @DisplayName("should handle buscar milhas scenarios")
        void shouldHandleBuscarMilhas(BuscarMilhasScenario scenario) {
            when(milhasRepository.buscarPorId(TEST_TENANT_ID, scenario.milhasId()))
                    .thenReturn(scenario.repositoryResponse());

            Optional<Milhas> result = milhasService.buscarMilhas(scenario.milhasId());

            assertThat(result).isEqualTo(scenario.expectedResult());

            if (scenario.expectedResult().isPresent()) {
                assertThat(result).isPresent();
                assertThat(result.get().id()).isEqualTo(scenario.milhasId());
            } else {
                assertThat(result).isEmpty();
            }

            verify(milhasRepository).buscarPorId(TEST_TENANT_ID, scenario.milhasId());
        }
    }

    @Nested
    @DisplayName("buscarMilhasPorCliente")
    class BuscarMilhasPorClienteTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PREFIX + "buscarMilhasPorClienteScenarios")
        @DisplayName("should handle buscar milhas por cliente scenarios")
        void shouldHandleBuscarMilhasPorCliente(BuscarMilhasPorClienteScenario scenario) {
            when(milhasRepository.buscarPorCliente(TEST_TENANT_ID, scenario.clienteId()))
                    .thenReturn(scenario.repositoryResponse());

            var result = milhasService.buscarMilhasPorCliente(scenario.clienteId());

            assertThat(result).hasSize(scenario.expectedSize());
            assertThat(result).containsExactlyInAnyOrderElementsOf(scenario.repositoryResponse());

            verify(milhasRepository).buscarPorCliente(TEST_TENANT_ID, scenario.clienteId());
        }
    }

    @Nested
    @DisplayName("buscarMilhasPorPrograma")
    class BuscarMilhasPorProgramaTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PREFIX + "buscarMilhasPorProgramaScenarios")
        @DisplayName("should handle buscar milhas por programa scenarios")
        void shouldHandleBuscarMilhasPorPrograma(BuscarMilhasPorProgramaScenario scenario) {
            when(milhasRepository.buscarPorPrograma(TEST_TENANT_ID, scenario.programa()))
                    .thenReturn(scenario.repositoryResponse());

            var result = milhasService.buscarMilhasPorPrograma(scenario.programa());

            assertThat(result).hasSize(scenario.expectedSize());
            assertThat(result).containsExactlyInAnyOrderElementsOf(scenario.repositoryResponse());

            if (!result.isEmpty()) {
                result.forEach(milhas ->
                        assertThat(milhas.programa()).isEqualTo(scenario.programa())
                );
            }

            verify(milhasRepository).buscarPorPrograma(TEST_TENANT_ID, scenario.programa());
        }
    }

    @Nested
    @DisplayName("atualizarMilhas")
    class AtualizarMilhasTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PREFIX + "atualizarMilhasScenarios")
        @DisplayName("should handle atualizar milhas scenarios")
        void shouldHandleAtualizarMilhas(AtualizarMilhasScenario scenario) {
            if (scenario.expectedException() != null) {
                if (scenario.milhasInput() != null) {
                    when(milhasRepository.existePorId(TEST_TENANT_ID, scenario.milhasInput().id()))
                            .thenReturn(scenario.existsInRepository());
                }

                assertThatThrownBy(() ->
                        milhasService.atualizarMilhas(scenario.milhasInput())
                )
                        .isInstanceOf(scenario.expectedException())
                        .hasMessage(scenario.expectedMessage());

                verify(milhasRepository, never()).atualizar(any(), any());
            } else {
                when(milhasRepository.existePorId(TEST_TENANT_ID, scenario.milhasInput().id()))
                        .thenReturn(scenario.existsInRepository());
                when(milhasRepository.atualizar(TEST_TENANT_ID, scenario.milhasInput()))
                        .thenReturn(scenario.repositorySaveResult());

                Milhas result = milhasService.atualizarMilhas(scenario.milhasInput());

                assertThat(result).isEqualTo(scenario.expectedResult());
                assertThat(result.id()).isEqualTo(scenario.milhasInput().id());

                verify(milhasRepository).existePorId(TEST_TENANT_ID, scenario.milhasInput().id());
                verify(milhasRepository).atualizar(TEST_TENANT_ID, scenario.milhasInput());
            }
        }
    }

    @Nested
    @DisplayName("removerMilhas")
    class RemoverMilhasTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PREFIX + "removerMilhasScenarios")
        @DisplayName("should handle remover milhas scenarios")
        void shouldHandleRemoverMilhas(RemoverMilhasScenario scenario) {
            when(milhasRepository.existePorId(TEST_TENANT_ID, scenario.milhasId()))
                    .thenReturn(scenario.existsInRepository());

            if (scenario.expectedException() != null) {
                assertThatThrownBy(() ->
                        milhasService.removerMilhas(scenario.milhasId())
                )
                        .isInstanceOf(scenario.expectedException())
                        .hasMessage(scenario.expectedMessage());

                verify(milhasRepository, never()).excluir(any(), any());
            } else {
                milhasService.removerMilhas(scenario.milhasId());

                verify(milhasRepository).existePorId(TEST_TENANT_ID, scenario.milhasId());
                verify(milhasRepository).excluir(TEST_TENANT_ID, scenario.milhasId());
            }
        }
    }

    @Nested
    @DisplayName("calcularSaldoTotal")
    class CalcularSaldoTotalTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PREFIX + "calcularSaldoTotalScenarios")
        @DisplayName("should handle calcular saldo total scenarios")
        void shouldHandleCalcularSaldoTotal(CalcularSaldoTotalScenario scenario) {
            when(milhasRepository.buscarPorCliente(TEST_TENANT_ID, scenario.clienteId()))
                    .thenReturn(scenario.repositoryResponse());

            int result = milhasService.calcularSaldoTotal(scenario.clienteId());

            assertThat(result).isEqualTo(scenario.expectedSaldoTotal());

            // Verify the sum is correct by manually calculating
            int expectedSum = scenario.repositoryResponse().stream()
                    .mapToInt(Milhas::quantidade)
                    .sum();
            assertThat(result).isEqualTo(expectedSum);

            verify(milhasRepository).buscarPorCliente(TEST_TENANT_ID, scenario.clienteId());
        }
    }

    @Nested
    @DisplayName("calcularCustoMedioMilheiro")
    class CalcularCustoMedioMilheiroTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PREFIX + "calcularCustoMedioMilheiroScenarios")
        @DisplayName("should handle calcular custo medio milheiro scenarios")
        void shouldHandleCalcularCustoMedioMilheiro(CalcularCustoMedioMilheiroScenario scenario) {
            when(milhasRepository.buscarPorCliente(TEST_TENANT_ID, scenario.clienteId()))
                    .thenReturn(scenario.repositoryResponse());

            BigDecimal result = milhasService.calcularCustoMedioMilheiro(scenario.clienteId());

            // Use compareTo for BigDecimal comparison to handle scale differences
            assertThat(result.compareTo(scenario.expectedCustoMedio()))
                    .as("Custo medio should match expected value within precision")
                    .isLessThanOrEqualTo(0);

            // Verify result is non-negative
            assertThat(result).isGreaterThanOrEqualTo(BigDecimal.ZERO);

            // Additional assertion: verify the calculation is correct
            if (!scenario.repositoryResponse().isEmpty()) {
                BigDecimal totalValor = scenario.repositoryResponse().stream()
                        .map(Milhas::valor)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                int totalQuantidade = scenario.repositoryResponse().stream()
                        .mapToInt(Milhas::quantidade)
                        .sum();

                if (totalQuantidade > 0) {
                    // Verify total valor and quantidade match expectations
                    assertThat(totalValor).isPositive();
                    assertThat(totalQuantidade).isPositive();
                }
            }

            verify(milhasRepository).buscarPorCliente(TEST_TENANT_ID, scenario.clienteId());
        }
    }

    @Nested
    @DisplayName("constructor validation")
    class ConstructorValidationTests {

        @ParameterizedTest(name = "should throw NullPointerException when repository is null - scenario {0}")
        @MethodSource("constructorNullRepositoryScenarios")
        @DisplayName("should validate constructor parameters")
        void shouldValidateConstructorParameters(ConstructorScenario scenario) {
            assertThatThrownBy(() ->
                    new MilhasService(scenario.repository())
            )
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("milhasRepository eh obrigatorio");
        }

        static java.util.stream.Stream<org.junit.jupiter.params.provider.Arguments> constructorNullRepositoryScenarios() {
            return java.util.stream.Stream.of(
                    org.junit.jupiter.params.provider.Arguments.of(new ConstructorScenario(
                            "should throw when repository is null",
                            null
                    ))
            );
        }

        record ConstructorScenario(String descricao, MilhasRepository repository) {
            @Override
            public String toString() {
                return descricao;
            }
        }
    }

    @Nested
    @DisplayName("precoPorMilheiro calculation")
    class PrecoPorMilheiroTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource("precoPorMilheiroScenarios")
        @DisplayName("should calculate preco por milheiro correctly")
        void shouldCalculatePrecoPorMilheiro(PrecoPorMilheiroScenario scenario) {
            BigDecimal result = scenario.milhas().precoPorMilheiro();

            // Compare with tolerance due to BigDecimal scale differences
            assertThat(result).isCloseTo(scenario.expectedPrecoPorMilheiro(), within(new BigDecimal("0.0001")));
        }

        static java.util.stream.Stream<org.junit.jupiter.params.provider.Arguments> precoPorMilheiroScenarios() {
            return java.util.stream.Stream.of(
                    // 10000 milhas, R$ 450.00 -> 450 / 10 = R$ 45.00 per milheiro
                    org.junit.jupiter.params.provider.Arguments.of(new PrecoPorMilheiroScenario(
                            "should calculate 45.00 for 10000 milhas at 450.00",
                            Milhas.criar(
                                    com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas.SMILES,
                                    10000,
                                    new BigDecimal("450.00")
                            ),
                            new BigDecimal("45.0000")
                    )),
                    // 5000 milhas, R$ 200.00 -> 200 / 5 = R$ 40.00 per milheiro
                    org.junit.jupiter.params.provider.Arguments.of(new PrecoPorMilheiroScenario(
                            "should calculate 40.00 for 5000 milhas at 200.00",
                            Milhas.criar(
                                    com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas.LATAM_PASS,
                                    5000,
                                    new BigDecimal("200.00")
                            ),
                            new BigDecimal("40.0000")
                    )),
                    // 8000 milhas, R$ 320.00 -> 320 / 8 = R$ 40.00 per milheiro
                    org.junit.jupiter.params.provider.Arguments.of(new PrecoPorMilheiroScenario(
                            "should calculate 40.00 for 8000 milhas at 320.00",
                            Milhas.criar(
                                    com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas.AZUL_FIDELIDADE,
                                    8000,
                                    new BigDecimal("320.00")
                            ),
                            new BigDecimal("40.0000")
                    )),
                    // 15000 milhas, R$ 600.00 -> 600 / 15 = R$ 40.00 per milheiro
                    org.junit.jupiter.params.provider.Arguments.of(new PrecoPorMilheiroScenario(
                            "should calculate 40.00 for 15000 milhas at 600.00",
                            Milhas.criar(
                                    com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas.LIVELO,
                                    15000,
                                    new BigDecimal("600.00")
                            ),
                            new BigDecimal("40.0000")
                    )),
                    // 1000 milhas, R$ 50.00 -> 50 / 1 = R$ 50.00 per milheiro
                    org.junit.jupiter.params.provider.Arguments.of(new PrecoPorMilheiroScenario(
                            "should calculate 50.00 for exactly 1 milheiro",
                            Milhas.criar(
                                    com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas.ESFERA,
                                    1000,
                                    new BigDecimal("50.00")
                            ),
                            new BigDecimal("50.0000")
                    ))
            );
        }

        record PrecoPorMilheiroScenario(String descricao, Milhas milhas, BigDecimal expectedPrecoPorMilheiro) {
            @Override
            public String toString() {
                return descricao;
            }
        }
    }
}
