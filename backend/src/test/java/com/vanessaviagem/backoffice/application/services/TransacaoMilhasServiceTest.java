package com.vanessaviagem.backoffice.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vanessaviagem.backoffice.application.ports.in.RegistrarTransacaoUseCase.TransacaoResult;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarTransacaoUseCase.VendaResult;
import com.vanessaviagem.backoffice.application.ports.out.ContaProgramaRepository;
import com.vanessaviagem.backoffice.application.ports.out.TransacaoRepository;
import com.vanessaviagem.backoffice.application.services.TransacaoMilhasServiceScenarios.*;
import com.vanessaviagem.backoffice.domain.model.ContaPrograma;
import com.vanessaviagem.backoffice.domain.model.Transacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

/**
 * Parameterized unit tests for TransacaoMilhasService.
 * Tests all use cases: registrar transacoes, consultar contas, and consultar transacoes.
 * All tests use scenario objects from external data providers.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TransacaoMilhasService")
class TransacaoMilhasServiceTest {

    private static final String DATA_PROVIDER_PREFIX =
            "com.vanessaviagem.backoffice.application.services.TransacaoMilhasServiceTestDataProvider#";

    @Mock
    private ContaProgramaRepository contaProgramaRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    private TransacaoMilhasService service;

    @BeforeEach
    void setUp() {
        service = new TransacaoMilhasService(contaProgramaRepository, transacaoRepository);
    }

    // ==================== REGISTRAR COMPRA TESTS ====================

    @Nested
    @DisplayName("registrarCompra")
    class RegistrarCompraTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PREFIX + "registrarCompraScenarios")
        @DisplayName("should handle registrar compra scenarios")
        void shouldHandleRegistrarCompra(RegistrarCompraScenario scenario) {
            if (scenario.expectedException() != null) {
                assertThatThrownBy(() -> service.registrarCompra(scenario.command()))
                        .isInstanceOf(scenario.expectedException())
                        .hasMessageContaining(scenario.expectedMessage());
            } else {
                // Setup mocks
                when(contaProgramaRepository.buscarPorTenantProgramaEOwner(
                        scenario.command().tenantId(),
                        scenario.command().programaId(),
                        scenario.command().owner()
                )).thenReturn(scenario.contaExistente());

                if (scenario.deveCriarConta()) {
                    when(contaProgramaRepository.salvar(any(ContaPrograma.class)))
                            .thenAnswer(inv -> {
                                ContaPrograma input = inv.getArgument(0);
                                return scenario.contaSalvaOuAtualizada();
                            });
                }

                when(contaProgramaRepository.atualizar(any(ContaPrograma.class)))
                        .thenReturn(scenario.contaSalvaOuAtualizada());

                when(transacaoRepository.salvar(any(Transacao.class)))
                        .thenReturn(scenario.transacaoSalva());

                // Execute
                TransacaoResult result = service.registrarCompra(scenario.command());

                // Verify
                assertThat(result).isNotNull();
                assertThat(result.transacao()).isNotNull();
                assertThat(result.contaAtualizada()).isNotNull();
                assertThat(result.contaAtualizada().saldoMilhas())
                        .isEqualTo(scenario.contaSalvaOuAtualizada().saldoMilhas());

                verify(contaProgramaRepository).buscarPorTenantProgramaEOwner(
                        scenario.command().tenantId(),
                        scenario.command().programaId(),
                        scenario.command().owner()
                );
                verify(transacaoRepository).salvar(any(Transacao.class));
            }
        }
    }

    // ==================== REGISTRAR BONUS TESTS ====================

    @Nested
    @DisplayName("registrarBonus")
    class RegistrarBonusTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PREFIX + "registrarBonusScenarios")
        @DisplayName("should handle registrar bonus scenarios")
        void shouldHandleRegistrarBonus(RegistrarBonusScenario scenario) {
            if (scenario.expectedException() != null) {
                assertThatThrownBy(() -> service.registrarBonus(scenario.command()))
                        .isInstanceOf(scenario.expectedException())
                        .hasMessageContaining(scenario.expectedMessage());
            } else {
                // Setup mocks
                when(contaProgramaRepository.buscarPorTenantProgramaEOwner(
                        scenario.command().tenantId(),
                        scenario.command().programaId(),
                        scenario.command().owner()
                )).thenReturn(scenario.contaExistente());

                if (scenario.deveCriarConta()) {
                    when(contaProgramaRepository.salvar(any(ContaPrograma.class)))
                            .thenAnswer(inv -> scenario.contaSalvaOuAtualizada());
                }

                when(contaProgramaRepository.atualizar(any(ContaPrograma.class)))
                        .thenReturn(scenario.contaSalvaOuAtualizada());

                when(transacaoRepository.salvar(any(Transacao.class)))
                        .thenReturn(scenario.transacaoSalva());

                // Execute
                TransacaoResult result = service.registrarBonus(scenario.command());

                // Verify
                assertThat(result).isNotNull();
                assertThat(result.transacao()).isNotNull();
                assertThat(result.contaAtualizada()).isNotNull();
                assertThat(result.contaAtualizada().saldoMilhas())
                        .isEqualTo(scenario.contaSalvaOuAtualizada().saldoMilhas());
                // Bonus should not increase cost base
                assertThat(result.contaAtualizada().custoBaseTotalBRL())
                        .isEqualByComparingTo(scenario.contaSalvaOuAtualizada().custoBaseTotalBRL());

                verify(transacaoRepository).salvar(any(Transacao.class));
            }
        }
    }

    // ==================== REGISTRAR VENDA TESTS ====================

    @Nested
    @DisplayName("registrarVenda")
    class RegistrarVendaTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PREFIX + "registrarVendaScenarios")
        @DisplayName("should handle registrar venda scenarios")
        void shouldHandleRegistrarVenda(RegistrarVendaScenario scenario) {
            // Setup mock for account lookup
            when(contaProgramaRepository.buscarPorTenantProgramaEOwner(
                    scenario.command().tenantId(),
                    scenario.command().programaId(),
                    scenario.command().owner()
            )).thenReturn(scenario.contaExistente());

            if (scenario.expectedException() != null) {
                assertThatThrownBy(() -> service.registrarVenda(scenario.command()))
                        .isInstanceOf(scenario.expectedException())
                        .hasMessageContaining(scenario.expectedMessage());

                verify(transacaoRepository, never()).salvar(any());
            } else {
                when(contaProgramaRepository.atualizar(any(ContaPrograma.class)))
                        .thenReturn(scenario.contaAtualizada());

                when(transacaoRepository.salvar(any(Transacao.class)))
                        .thenReturn(scenario.transacaoSalva());

                // Execute
                VendaResult result = service.registrarVenda(scenario.command());

                // Verify
                assertThat(result).isNotNull();
                assertThat(result.transacao()).isNotNull();
                assertThat(result.contaAtualizada()).isNotNull();
                assertThat(result.contaAtualizada().saldoMilhas())
                        .isEqualTo(scenario.contaAtualizada().saldoMilhas());

                // Verify profit/loss sign matches expected
                if (scenario.lucroEsperado().signum() > 0) {
                    assertThat(result.teveLucro()).isTrue();
                } else if (scenario.lucroEsperado().signum() < 0) {
                    assertThat(result.tevePrejuizo()).isTrue();
                }

                verify(contaProgramaRepository).atualizar(any(ContaPrograma.class));
                verify(transacaoRepository).salvar(any(Transacao.class));
            }
        }
    }

    // ==================== BUSCAR POR ID TESTS ====================

    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorIdTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PREFIX + "buscarPorIdScenarios")
        @DisplayName("should handle buscar por id scenarios")
        void shouldHandleBuscarPorId(BuscarPorIdScenario scenario) {
            when(contaProgramaRepository.buscarPorId(scenario.tenantId(), scenario.contaId()))
                    .thenReturn(scenario.repositoryResponse());

            Optional<ContaPrograma> result = service.buscarPorId(scenario.tenantId(), scenario.contaId());

            assertThat(result.isPresent()).isEqualTo(scenario.expectedPresent());

            if (scenario.expectedPresent()) {
                assertThat(result.get().id()).isEqualTo(scenario.contaId());
            }

            verify(contaProgramaRepository).buscarPorId(scenario.tenantId(), scenario.contaId());
        }
    }

    // ==================== LISTAR POR OWNER TESTS ====================

    @Nested
    @DisplayName("listarPorOwner")
    class ListarPorOwnerTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PREFIX + "listarPorOwnerScenarios")
        @DisplayName("should handle listar por owner scenarios")
        void shouldHandleListarPorOwner(ListarPorOwnerScenario scenario) {
            if (scenario.expectedException() != null) {
                assertThatThrownBy(() -> service.listarPorOwner(scenario.tenantId(), scenario.owner()))
                        .isInstanceOf(scenario.expectedException())
                        .hasMessageContaining(scenario.expectedMessage());

                verify(contaProgramaRepository, never()).buscarPorOwner(any(), any());
            } else {
                when(contaProgramaRepository.buscarPorOwner(scenario.tenantId(), scenario.owner()))
                        .thenReturn(scenario.repositoryResponse());

                var result = service.listarPorOwner(scenario.tenantId(), scenario.owner());

                assertThat(result).hasSize(scenario.expectedSize());
                assertThat(result).containsExactlyInAnyOrderElementsOf(scenario.repositoryResponse());

                verify(contaProgramaRepository).buscarPorOwner(scenario.tenantId(), scenario.owner());
            }
        }
    }

    // ==================== LISTAR TODOS TESTS ====================

    @Nested
    @DisplayName("listarTodos")
    class ListarTodosTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PREFIX + "listarTodosScenarios")
        @DisplayName("should handle listar todos scenarios")
        void shouldHandleListarTodos(ListarTodosScenario scenario) {
            when(contaProgramaRepository.buscarTodos(scenario.tenantId()))
                    .thenReturn(scenario.repositoryResponse());

            var result = service.listarTodos(scenario.tenantId());

            assertThat(result).hasSize(scenario.expectedSize());
            assertThat(result).containsExactlyInAnyOrderElementsOf(scenario.repositoryResponse());

            verify(contaProgramaRepository).buscarTodos(scenario.tenantId());
        }
    }

    // ==================== TOTAL MILHAS TESTS ====================

    @Nested
    @DisplayName("totalMilhas")
    class TotalMilhasTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PREFIX + "totalMilhasScenarios")
        @DisplayName("should handle total milhas scenarios")
        void shouldHandleTotalMilhas(TotalMilhasScenario scenario) {
            when(contaProgramaRepository.calcularTotalMilhas(scenario.tenantId()))
                    .thenReturn(scenario.repositoryResponse());

            long result = service.totalMilhas(scenario.tenantId());

            assertThat(result).isEqualTo(scenario.expectedTotal());

            verify(contaProgramaRepository).calcularTotalMilhas(scenario.tenantId());
        }
    }

    // ==================== TOTAIS POR OWNER TESTS ====================

    @Nested
    @DisplayName("totaisPorOwner")
    class TotaisPorOwnerTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PREFIX + "totaisPorOwnerScenarios")
        @DisplayName("should handle totais por owner scenarios")
        void shouldHandleTotaisPorOwner(TotaisPorOwnerScenario scenario) {
            when(contaProgramaRepository.calcularTotaisPorOwner(scenario.tenantId()))
                    .thenReturn(scenario.repositoryResponse());

            var result = service.totaisPorOwner(scenario.tenantId());

            assertThat(result).hasSize(scenario.expectedMapSize());
            assertThat(result).containsAllEntriesOf(scenario.repositoryResponse());

            verify(contaProgramaRepository).calcularTotaisPorOwner(scenario.tenantId());
        }
    }

    // ==================== TOTAIS POR PROGRAMA TESTS ====================

    @Nested
    @DisplayName("totaisPorPrograma")
    class TotaisPorProgramaTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PREFIX + "totaisPorProgramaScenarios")
        @DisplayName("should handle totais por programa scenarios")
        void shouldHandleTotaisPorPrograma(TotaisPorProgramaScenario scenario) {
            when(contaProgramaRepository.calcularTotaisPorPrograma(scenario.tenantId()))
                    .thenReturn(scenario.repositoryResponse());

            var result = service.totaisPorPrograma(scenario.tenantId());

            assertThat(result).hasSize(scenario.expectedMapSize());
            assertThat(result).containsAllEntriesOf(scenario.repositoryResponse());

            verify(contaProgramaRepository).calcularTotaisPorPrograma(scenario.tenantId());
        }
    }

    // ==================== LISTAR TRANSACOES POR CONTA TESTS ====================

    @Nested
    @DisplayName("listarPorContaPrograma")
    class ListarTransacoesPorContaTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PREFIX + "listarTransacoesPorContaScenarios")
        @DisplayName("should handle listar transacoes por conta scenarios")
        void shouldHandleListarTransacoesPorConta(ListarTransacoesPorContaScenario scenario) {
            when(transacaoRepository.buscarPorContaPrograma(scenario.contaProgramaId()))
                    .thenReturn(scenario.repositoryResponse());

            var result = service.listarPorContaPrograma(scenario.contaProgramaId());

            assertThat(result).hasSize(scenario.expectedSize());
            assertThat(result).containsExactlyInAnyOrderElementsOf(scenario.repositoryResponse());

            verify(transacaoRepository).buscarPorContaPrograma(scenario.contaProgramaId());
        }
    }

    // ==================== LISTAR TRANSACOES POR PERIODO TESTS ====================

    @Nested
    @DisplayName("listarPorPeriodo")
    class ListarTransacoesPorPeriodoTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PREFIX + "listarTransacoesPorPeriodoScenarios")
        @DisplayName("should handle listar transacoes por periodo scenarios")
        void shouldHandleListarTransacoesPorPeriodo(ListarTransacoesPorPeriodoScenario scenario) {
            if (scenario.expectedException() != null) {
                assertThatThrownBy(() -> service.listarPorPeriodo(
                        scenario.contaProgramaId(),
                        scenario.inicio(),
                        scenario.fim()
                ))
                        .isInstanceOf(scenario.expectedException())
                        .hasMessageContaining(scenario.expectedMessage());

                verify(transacaoRepository, never()).buscarPorPeriodo(any(), any(), any());
            } else {
                when(transacaoRepository.buscarPorPeriodo(
                        scenario.contaProgramaId(),
                        scenario.inicio(),
                        scenario.fim()
                )).thenReturn(scenario.repositoryResponse());

                var result = service.listarPorPeriodo(
                        scenario.contaProgramaId(),
                        scenario.inicio(),
                        scenario.fim()
                );

                assertThat(result).hasSize(scenario.expectedSize());
                assertThat(result).containsExactlyInAnyOrderElementsOf(scenario.repositoryResponse());

                verify(transacaoRepository).buscarPorPeriodo(
                        scenario.contaProgramaId(),
                        scenario.inicio(),
                        scenario.fim()
                );
            }
        }
    }

    // ==================== LISTAR TRANSACOES POR TIPO TESTS ====================

    @Nested
    @DisplayName("listarPorTipo")
    class ListarTransacoesPorTipoTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PREFIX + "listarTransacoesPorTipoScenarios")
        @DisplayName("should handle listar transacoes por tipo scenarios")
        void shouldHandleListarTransacoesPorTipo(ListarTransacoesPorTipoScenario scenario) {
            when(transacaoRepository.buscarPorTipo(scenario.contaProgramaId(), scenario.tipo()))
                    .thenReturn(scenario.repositoryResponse());

            var result = service.listarPorTipo(scenario.contaProgramaId(), scenario.tipo());

            assertThat(result).hasSize(scenario.expectedSize());
            assertThat(result).containsExactlyInAnyOrderElementsOf(scenario.repositoryResponse());

            // Verify all returned transactions are of the correct type
            result.forEach(t -> assertThat(t.tipo()).isEqualTo(scenario.tipo()));

            verify(transacaoRepository).buscarPorTipo(scenario.contaProgramaId(), scenario.tipo());
        }
    }

    // ==================== CONSTRUCTOR VALIDATION TESTS ====================

    @Nested
    @DisplayName("constructor validation")
    class ConstructorValidationTests {

        @ParameterizedTest(name = "{0}")
        @MethodSource(DATA_PROVIDER_PREFIX + "constructorScenarios")
        @DisplayName("should validate constructor parameters")
        void shouldValidateConstructorParameters(ConstructorScenario scenario) {
            ContaProgramaRepository contaRepo = scenario.contaRepoNull() ? null : contaProgramaRepository;
            TransacaoRepository transacaoRepo = scenario.transacaoRepoNull() ? null : transacaoRepository;

            assertThatThrownBy(() -> new TransacaoMilhasService(contaRepo, transacaoRepo))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining(scenario.expectedMessage());
        }
    }
}
