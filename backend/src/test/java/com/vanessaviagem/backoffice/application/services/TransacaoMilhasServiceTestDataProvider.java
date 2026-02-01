package com.vanessaviagem.backoffice.application.services;

import com.vanessaviagem.backoffice.application.ports.in.RegistrarTransacaoUseCase.RegistrarBonusCommand;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarTransacaoUseCase.RegistrarCompraCommand;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarTransacaoUseCase.RegistrarVendaCommand;
import com.vanessaviagem.backoffice.application.services.TransacaoMilhasServiceScenarios.*;
import com.vanessaviagem.backoffice.domain.model.ContaPrograma;
import com.vanessaviagem.backoffice.domain.model.Transacao;
import com.vanessaviagem.backoffice.domain.model.enums.TipoTransacao;
import org.junit.jupiter.params.provider.Arguments;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * External data provider for TransacaoMilhasService parameterized tests.
 * All scenarios are provided via @JvmStatic methods.
 */
public final class TransacaoMilhasServiceTestDataProvider {

    // Fixed IDs for test consistency
    private static final UUID TENANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID PROGRAMA_SMILES_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID PROGRAMA_LATAM_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID CONTA_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID TRANSACAO_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");

    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final LocalDateTime YESTERDAY = NOW.minusDays(1);
    private static final LocalDateTime LAST_WEEK = NOW.minusWeeks(1);

    private TransacaoMilhasServiceTestDataProvider() {
        // Utility class
    }

    // ==================== REGISTRAR COMPRA SCENARIOS ====================

    public static Stream<Arguments> registrarCompraScenarios() {
        return Stream.of(
                // Cenario 1: Compra com conta nova
                Arguments.of(new RegistrarCompraScenario(
                        "should create new account and register purchase",
                        new RegistrarCompraCommand(
                                TENANT_ID,
                                PROGRAMA_SMILES_ID,
                                "Smiles",
                                "Diego",
                                10000L,
                                new BigDecimal("350.00"),
                                "Compra direta",
                                "Black Friday"
                        ),
                        Optional.empty(), // Conta nao existe
                        createContaPrograma(CONTA_ID, TENANT_ID, PROGRAMA_SMILES_ID, "Smiles", "Diego",
                                10000L, new BigDecimal("350.00"), new BigDecimal("35.000000")),
                        createTransacao(TRANSACAO_ID, CONTA_ID, TipoTransacao.COMPRA, 10000L,
                                new BigDecimal("350.00"), "Compra direta", "Black Friday"),
                        true, // deve criar conta
                        null,
                        null
                )),

                // Cenario 2: Compra com conta existente
                Arguments.of(new RegistrarCompraScenario(
                        "should use existing account and update balance on purchase",
                        new RegistrarCompraCommand(
                                TENANT_ID,
                                PROGRAMA_SMILES_ID,
                                "Smiles",
                                "Diego",
                                5000L,
                                new BigDecimal("150.00"),
                                "Promocao",
                                null
                        ),
                        Optional.of(createContaPrograma(CONTA_ID, TENANT_ID, PROGRAMA_SMILES_ID, "Smiles", "Diego",
                                10000L, new BigDecimal("350.00"), new BigDecimal("35.000000"))),
                        createContaPrograma(CONTA_ID, TENANT_ID, PROGRAMA_SMILES_ID, "Smiles", "Diego",
                                15000L, new BigDecimal("500.00"), new BigDecimal("33.333333")),
                        createTransacao(TRANSACAO_ID, CONTA_ID, TipoTransacao.COMPRA, 5000L,
                                new BigDecimal("150.00"), "Promocao", null),
                        false, // nao deve criar conta
                        null,
                        null
                )),

                // Cenario 3: Compra com valor alto (custo medio menor)
                Arguments.of(new RegistrarCompraScenario(
                        "should calculate correct average cost on large purchase",
                        new RegistrarCompraCommand(
                                TENANT_ID,
                                PROGRAMA_LATAM_ID,
                                "LATAM Pass",
                                "Vanessa",
                                50000L,
                                new BigDecimal("1000.00"),
                                "Livelo transferencia",
                                "R$20 por milheiro"
                        ),
                        Optional.empty(),
                        createContaPrograma(CONTA_ID, TENANT_ID, PROGRAMA_LATAM_ID, "LATAM Pass", "Vanessa",
                                50000L, new BigDecimal("1000.00"), new BigDecimal("20.000000")),
                        createTransacao(TRANSACAO_ID, CONTA_ID, TipoTransacao.COMPRA, 50000L,
                                new BigDecimal("1000.00"), "Livelo transferencia", "R$20 por milheiro"),
                        true,
                        null,
                        null
                ))
        );
    }

    // ==================== REGISTRAR BONUS SCENARIOS ====================

    public static Stream<Arguments> registrarBonusScenarios() {
        return Stream.of(
                // Cenario 1: Bonus com conta nova (primeiro registro)
                Arguments.of(new RegistrarBonusScenario(
                        "should create account and register bonus with zero cost",
                        new RegistrarBonusCommand(
                                TENANT_ID,
                                PROGRAMA_SMILES_ID,
                                "Smiles",
                                "Pai",
                                5000L,
                                "Cashback Santander",
                                "Gastos mensais"
                        ),
                        Optional.empty(),
                        createContaPrograma(CONTA_ID, TENANT_ID, PROGRAMA_SMILES_ID, "Smiles", "Pai",
                                5000L, BigDecimal.ZERO, BigDecimal.ZERO),
                        createTransacao(TRANSACAO_ID, CONTA_ID, TipoTransacao.BONUS, 5000L,
                                BigDecimal.ZERO, "Cashback Santander", "Gastos mensais"),
                        true,
                        null,
                        null
                )),

                // Cenario 2: Bonus dilui custo medio
                Arguments.of(new RegistrarBonusScenario(
                        "should dilute average cost when adding bonus to existing account",
                        new RegistrarBonusCommand(
                                TENANT_ID,
                                PROGRAMA_SMILES_ID,
                                "Smiles",
                                "Diego",
                                10000L,
                                "Promocao Smiles",
                                "Dobro de milhas"
                        ),
                        Optional.of(createContaPrograma(CONTA_ID, TENANT_ID, PROGRAMA_SMILES_ID, "Smiles", "Diego",
                                10000L, new BigDecimal("350.00"), new BigDecimal("35.000000"))),
                        createContaPrograma(CONTA_ID, TENANT_ID, PROGRAMA_SMILES_ID, "Smiles", "Diego",
                                20000L, new BigDecimal("350.00"), new BigDecimal("17.500000")),
                        createTransacao(TRANSACAO_ID, CONTA_ID, TipoTransacao.BONUS, 10000L,
                                BigDecimal.ZERO, "Promocao Smiles", "Dobro de milhas"),
                        false,
                        null,
                        null
                )),

                // Cenario 3: Bonus grande em conta existente
                Arguments.of(new RegistrarBonusScenario(
                        "should handle large bonus correctly",
                        new RegistrarBonusCommand(
                                TENANT_ID,
                                PROGRAMA_LATAM_ID,
                                "LATAM Pass",
                                "Mae",
                                20000L,
                                "Itau Personnalite",
                                "Aniversario"
                        ),
                        Optional.of(createContaPrograma(CONTA_ID, TENANT_ID, PROGRAMA_LATAM_ID, "LATAM Pass", "Mae",
                                5000L, new BigDecimal("200.00"), new BigDecimal("40.000000"))),
                        createContaPrograma(CONTA_ID, TENANT_ID, PROGRAMA_LATAM_ID, "LATAM Pass", "Mae",
                                25000L, new BigDecimal("200.00"), new BigDecimal("8.000000")),
                        createTransacao(TRANSACAO_ID, CONTA_ID, TipoTransacao.BONUS, 20000L,
                                BigDecimal.ZERO, "Itau Personnalite", "Aniversario"),
                        false,
                        null,
                        null
                ))
        );
    }

    // ==================== REGISTRAR VENDA SCENARIOS ====================

    public static Stream<Arguments> registrarVendaScenarios() {
        return Stream.of(
                // Cenario 1: Venda com lucro
                Arguments.of(new RegistrarVendaScenario(
                        "should register sale with profit when selling above cost",
                        new RegistrarVendaCommand(
                                TENANT_ID,
                                PROGRAMA_SMILES_ID,
                                "Smiles",
                                "Diego",
                                5000L,
                                new BigDecimal("250.00"),
                                "Venda MaxMilhas"
                        ),
                        Optional.of(createContaPrograma(CONTA_ID, TENANT_ID, PROGRAMA_SMILES_ID, "Smiles", "Diego",
                                10000L, new BigDecimal("350.00"), new BigDecimal("35.000000"))),
                        createContaPrograma(CONTA_ID, TENANT_ID, PROGRAMA_SMILES_ID, "Smiles", "Diego",
                                5000L, new BigDecimal("175.00"), new BigDecimal("35.000000")),
                        createTransacao(TRANSACAO_ID, CONTA_ID, TipoTransacao.VENDA, 5000L,
                                new BigDecimal("250.00"), null, "Venda MaxMilhas"),
                        new BigDecimal("75.00"), // lucro = 250 - 175
                        null,
                        null
                )),

                // Cenario 2: Venda com prejuizo
                Arguments.of(new RegistrarVendaScenario(
                        "should register sale with loss when selling below cost",
                        new RegistrarVendaCommand(
                                TENANT_ID,
                                PROGRAMA_SMILES_ID,
                                "Smiles",
                                "Diego",
                                5000L,
                                new BigDecimal("100.00"),
                                "Venda urgente"
                        ),
                        Optional.of(createContaPrograma(CONTA_ID, TENANT_ID, PROGRAMA_SMILES_ID, "Smiles", "Diego",
                                10000L, new BigDecimal("350.00"), new BigDecimal("35.000000"))),
                        createContaPrograma(CONTA_ID, TENANT_ID, PROGRAMA_SMILES_ID, "Smiles", "Diego",
                                5000L, new BigDecimal("175.00"), new BigDecimal("35.000000")),
                        createTransacao(TRANSACAO_ID, CONTA_ID, TipoTransacao.VENDA, 5000L,
                                new BigDecimal("100.00"), null, "Venda urgente"),
                        new BigDecimal("-75.00"), // prejuizo = 100 - 175
                        null,
                        null
                )),

                // Cenario 3: Venda total (zera conta)
                Arguments.of(new RegistrarVendaScenario(
                        "should zero account when selling all miles",
                        new RegistrarVendaCommand(
                                TENANT_ID,
                                PROGRAMA_LATAM_ID,
                                "LATAM Pass",
                                "Vanessa",
                                10000L,
                                new BigDecimal("400.00"),
                                "Venda completa"
                        ),
                        Optional.of(createContaPrograma(CONTA_ID, TENANT_ID, PROGRAMA_LATAM_ID, "LATAM Pass", "Vanessa",
                                10000L, new BigDecimal("300.00"), new BigDecimal("30.000000"))),
                        createContaPrograma(CONTA_ID, TENANT_ID, PROGRAMA_LATAM_ID, "LATAM Pass", "Vanessa",
                                0L, BigDecimal.ZERO, BigDecimal.ZERO),
                        createTransacao(TRANSACAO_ID, CONTA_ID, TipoTransacao.VENDA, 10000L,
                                new BigDecimal("400.00"), null, "Venda completa"),
                        new BigDecimal("100.00"), // lucro = 400 - 300
                        null,
                        null
                )),

                // Cenario 4: Venda sem conta existente (erro)
                Arguments.of(new RegistrarVendaScenario(
                        "should throw when selling from non-existent account",
                        new RegistrarVendaCommand(
                                TENANT_ID,
                                PROGRAMA_SMILES_ID,
                                "Smiles",
                                "OwnerInexistente",
                                5000L,
                                new BigDecimal("200.00"),
                                null
                        ),
                        Optional.empty(),
                        null,
                        null,
                        null,
                        IllegalStateException.class,
                        "ContaPrograma nao encontrada para programa=Smiles e owner=OwnerInexistente"
                ))
        );
    }

    // ==================== BUSCAR POR ID SCENARIOS ====================

    public static Stream<Arguments> buscarPorIdScenarios() {
        return Stream.of(
                Arguments.of(new BuscarPorIdScenario(
                        "should return account when found",
                        TENANT_ID,
                        CONTA_ID,
                        Optional.of(createContaPrograma(CONTA_ID, TENANT_ID, PROGRAMA_SMILES_ID, "Smiles", "Diego",
                                10000L, new BigDecimal("350.00"), new BigDecimal("35.000000"))),
                        true
                )),
                Arguments.of(new BuscarPorIdScenario(
                        "should return empty when not found",
                        TENANT_ID,
                        UUID.randomUUID(),
                        Optional.empty(),
                        false
                ))
        );
    }

    // ==================== LISTAR POR OWNER SCENARIOS ====================

    public static Stream<Arguments> listarPorOwnerScenarios() {
        return Stream.of(
                Arguments.of(new ListarPorOwnerScenario(
                        "should return accounts for owner",
                        TENANT_ID,
                        "Diego",
                        List.of(
                                createContaPrograma(CONTA_ID, TENANT_ID, PROGRAMA_SMILES_ID, "Smiles", "Diego",
                                        10000L, new BigDecimal("350.00"), new BigDecimal("35.000000")),
                                createContaPrograma(UUID.randomUUID(), TENANT_ID, PROGRAMA_LATAM_ID, "LATAM Pass", "Diego",
                                        25000L, new BigDecimal("750.00"), new BigDecimal("30.000000"))
                        ),
                        2,
                        null,
                        null
                )),
                Arguments.of(new ListarPorOwnerScenario(
                        "should return empty list when owner has no accounts",
                        TENANT_ID,
                        "OwnerSemContas",
                        Collections.emptyList(),
                        0,
                        null,
                        null
                )),
                Arguments.of(new ListarPorOwnerScenario(
                        "should throw when owner is blank",
                        TENANT_ID,
                        "   ",
                        null,
                        0,
                        IllegalArgumentException.class,
                        "owner nao pode estar vazio"
                ))
        );
    }

    // ==================== LISTAR TODOS SCENARIOS ====================

    public static Stream<Arguments> listarTodosScenarios() {
        return Stream.of(
                Arguments.of(new ListarTodosScenario(
                        "should return all accounts for tenant",
                        TENANT_ID,
                        List.of(
                                createContaPrograma(CONTA_ID, TENANT_ID, PROGRAMA_SMILES_ID, "Smiles", "Diego",
                                        10000L, new BigDecimal("350.00"), new BigDecimal("35.000000")),
                                createContaPrograma(UUID.randomUUID(), TENANT_ID, PROGRAMA_LATAM_ID, "LATAM Pass", "Vanessa",
                                        25000L, new BigDecimal("750.00"), new BigDecimal("30.000000")),
                                createContaPrograma(UUID.randomUUID(), TENANT_ID, PROGRAMA_SMILES_ID, "Smiles", "Pai",
                                        5000L, BigDecimal.ZERO, BigDecimal.ZERO)
                        ),
                        3
                )),
                Arguments.of(new ListarTodosScenario(
                        "should return empty list when tenant has no accounts",
                        UUID.randomUUID(),
                        Collections.emptyList(),
                        0
                ))
        );
    }

    // ==================== TOTAL MILHAS SCENARIOS ====================

    public static Stream<Arguments> totalMilhasScenarios() {
        return Stream.of(
                Arguments.of(new TotalMilhasScenario(
                        "should return sum of all miles",
                        TENANT_ID,
                        158000L,
                        158000L
                )),
                Arguments.of(new TotalMilhasScenario(
                        "should return zero when no accounts",
                        UUID.randomUUID(),
                        0L,
                        0L
                ))
        );
    }

    // ==================== TOTAIS POR OWNER SCENARIOS ====================

    public static Stream<Arguments> totaisPorOwnerScenarios() {
        return Stream.of(
                Arguments.of(new TotaisPorOwnerScenario(
                        "should return totals grouped by owner",
                        TENANT_ID,
                        Map.of(
                                "Diego", 85000L,
                                "Vanessa", 45000L,
                                "Pai", 20000L,
                                "Mae", 8000L
                        ),
                        4
                )),
                Arguments.of(new TotaisPorOwnerScenario(
                        "should return empty map when no accounts",
                        UUID.randomUUID(),
                        Collections.emptyMap(),
                        0
                ))
        );
    }

    // ==================== TOTAIS POR PROGRAMA SCENARIOS ====================

    public static Stream<Arguments> totaisPorProgramaScenarios() {
        return Stream.of(
                Arguments.of(new TotaisPorProgramaScenario(
                        "should return totals grouped by program",
                        TENANT_ID,
                        Map.of(
                                "Smiles", 25000L,
                                "LATAM Pass", 33000L,
                                "Livelo", 50000L,
                                "Azul Fidelidade", 30000L,
                                "Esfera", 20000L
                        ),
                        5
                )),
                Arguments.of(new TotaisPorProgramaScenario(
                        "should return empty map when no accounts",
                        UUID.randomUUID(),
                        Collections.emptyMap(),
                        0
                ))
        );
    }

    // ==================== LISTAR TRANSACOES POR CONTA SCENARIOS ====================

    public static Stream<Arguments> listarTransacoesPorContaScenarios() {
        return Stream.of(
                Arguments.of(new ListarTransacoesPorContaScenario(
                        "should return all transactions for account",
                        CONTA_ID,
                        List.of(
                                createTransacao(UUID.randomUUID(), CONTA_ID, TipoTransacao.COMPRA, 10000L,
                                        new BigDecimal("350.00"), "Compra direta", null),
                                createTransacao(UUID.randomUUID(), CONTA_ID, TipoTransacao.BONUS, 5000L,
                                        BigDecimal.ZERO, "Cashback", null),
                                createTransacao(UUID.randomUUID(), CONTA_ID, TipoTransacao.VENDA, 2000L,
                                        new BigDecimal("100.00"), null, "Venda")
                        ),
                        3
                )),
                Arguments.of(new ListarTransacoesPorContaScenario(
                        "should return empty list when account has no transactions",
                        UUID.randomUUID(),
                        Collections.emptyList(),
                        0
                ))
        );
    }

    // ==================== LISTAR TRANSACOES POR PERIODO SCENARIOS ====================

    public static Stream<Arguments> listarTransacoesPorPeriodoScenarios() {
        return Stream.of(
                Arguments.of(new ListarTransacoesPorPeriodoScenario(
                        "should return transactions within period",
                        CONTA_ID,
                        LAST_WEEK,
                        NOW,
                        List.of(
                                createTransacao(UUID.randomUUID(), CONTA_ID, TipoTransacao.COMPRA, 10000L,
                                        new BigDecimal("350.00"), "Compra", null),
                                createTransacao(UUID.randomUUID(), CONTA_ID, TipoTransacao.BONUS, 5000L,
                                        BigDecimal.ZERO, "Bonus", null)
                        ),
                        2,
                        null,
                        null
                )),
                Arguments.of(new ListarTransacoesPorPeriodoScenario(
                        "should throw when inicio is after fim",
                        CONTA_ID,
                        NOW,
                        LAST_WEEK,
                        null,
                        0,
                        IllegalArgumentException.class,
                        "inicio nao pode ser posterior a fim"
                ))
        );
    }

    // ==================== LISTAR TRANSACOES POR TIPO SCENARIOS ====================

    public static Stream<Arguments> listarTransacoesPorTipoScenarios() {
        return Stream.of(
                Arguments.of(new ListarTransacoesPorTipoScenario(
                        "should return only COMPRA transactions",
                        CONTA_ID,
                        TipoTransacao.COMPRA,
                        List.of(
                                createTransacao(UUID.randomUUID(), CONTA_ID, TipoTransacao.COMPRA, 10000L,
                                        new BigDecimal("350.00"), "Compra 1", null),
                                createTransacao(UUID.randomUUID(), CONTA_ID, TipoTransacao.COMPRA, 5000L,
                                        new BigDecimal("150.00"), "Compra 2", null)
                        ),
                        2
                )),
                Arguments.of(new ListarTransacoesPorTipoScenario(
                        "should return only BONUS transactions",
                        CONTA_ID,
                        TipoTransacao.BONUS,
                        List.of(
                                createTransacao(UUID.randomUUID(), CONTA_ID, TipoTransacao.BONUS, 5000L,
                                        BigDecimal.ZERO, "Cashback", null)
                        ),
                        1
                )),
                Arguments.of(new ListarTransacoesPorTipoScenario(
                        "should return empty when no transactions of type",
                        CONTA_ID,
                        TipoTransacao.VENDA,
                        Collections.emptyList(),
                        0
                ))
        );
    }

    // ==================== CONSTRUCTOR SCENARIOS ====================

    public static Stream<Arguments> constructorScenarios() {
        return Stream.of(
                Arguments.of(new ConstructorScenario(
                        "should throw when contaProgramaRepository is null",
                        true,
                        false,
                        "contaProgramaRepository eh obrigatorio"
                )),
                Arguments.of(new ConstructorScenario(
                        "should throw when transacaoRepository is null",
                        false,
                        true,
                        "transacaoRepository eh obrigatorio"
                ))
        );
    }

    // ==================== HELPER METHODS ====================

    private static ContaPrograma createContaPrograma(
            UUID id, UUID tenantId, UUID programaId, String programaNome, String owner,
            long saldoMilhas, BigDecimal custoBase, BigDecimal custoMedio
    ) {
        return new ContaPrograma(
                id,
                tenantId,
                programaId,
                programaNome,
                owner,
                saldoMilhas,
                custoBase,
                custoMedio,
                NOW.minusDays(30),
                NOW
        );
    }

    private static Transacao createTransacao(
            UUID id, UUID contaProgramaId, TipoTransacao tipo, long milhas,
            BigDecimal valor, String fonte, String observacao
    ) {
        return new Transacao(
                id,
                contaProgramaId,
                tipo,
                milhas,
                valor,
                fonte,
                observacao,
                NOW,
                NOW
        );
    }
}
