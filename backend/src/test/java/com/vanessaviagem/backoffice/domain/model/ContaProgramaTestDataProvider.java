package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.exceptions.SaldoMilhasInsuficienteException;
import com.vanessaviagem.backoffice.domain.model.ContaProgramaScenario.AplicarBonusInvalidoScenario;
import com.vanessaviagem.backoffice.domain.model.ContaProgramaScenario.AplicarBonusScenario;
import com.vanessaviagem.backoffice.domain.model.ContaProgramaScenario.AplicarCompraInvalidoScenario;
import com.vanessaviagem.backoffice.domain.model.ContaProgramaScenario.AplicarCompraScenario;
import com.vanessaviagem.backoffice.domain.model.ContaProgramaScenario.AplicarVendaInvalidoScenario;
import com.vanessaviagem.backoffice.domain.model.ContaProgramaScenario.AplicarVendaScenario;
import com.vanessaviagem.backoffice.domain.model.ContaProgramaScenario.ConstrutorInvalidoScenario;
import com.vanessaviagem.backoffice.domain.model.ContaProgramaScenario.CriarInvalidoScenario;
import com.vanessaviagem.backoffice.domain.model.ContaProgramaScenario.CriarScenario;
import com.vanessaviagem.backoffice.domain.model.ContaProgramaScenario.FluxoCompletoScenario;
import com.vanessaviagem.backoffice.domain.model.ContaProgramaScenario.PodeSacarScenario;
import com.vanessaviagem.backoffice.domain.model.ContaProgramaScenario.TemSaldoScenario;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

/**
 * Test data provider for ContaPrograma tests.
 */
public final class ContaProgramaTestDataProvider {

    private static final UUID TENANT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID TENANT_ID_2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID PROGRAMA_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID PROGRAMA_ID_2 = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final UUID CONTA_ID = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
    private static final LocalDateTime AGORA = LocalDateTime.of(2025, 1, 15, 10, 30, 0);

    private ContaProgramaTestDataProvider() {
    }

    /**
     * Creates a ContaPrograma with saldo for testing.
     */
    private static ContaPrograma contaComSaldo(long saldo, BigDecimal custoBase, BigDecimal custoMedio) {
        return new ContaPrograma(
                CONTA_ID,
                TENANT_ID,
                PROGRAMA_ID,
                "Smiles",
                "Joao Silva",
                saldo,
                custoBase,
                custoMedio,
                AGORA,
                AGORA
        );
    }

    /**
     * Creates a ContaPrograma with zero balance.
     */
    private static ContaPrograma contaZerada() {
        return new ContaPrograma(
                CONTA_ID,
                TENANT_ID,
                PROGRAMA_ID,
                "Smiles",
                "Joao Silva",
                0L,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                AGORA,
                AGORA
        );
    }

    public static Stream<Arguments> criarValido() {
        return Stream.of(
                Arguments.of(new CriarScenario(
                        "should create conta for Smiles program",
                        TENANT_ID,
                        PROGRAMA_ID,
                        "Smiles",
                        "Joao Silva"
                )),
                Arguments.of(new CriarScenario(
                        "should create conta for LATAM Pass program",
                        TENANT_ID_2,
                        PROGRAMA_ID_2,
                        "LATAM Pass",
                        "Maria Santos"
                )),
                Arguments.of(new CriarScenario(
                        "should create conta with whitespace trimmed",
                        TENANT_ID,
                        PROGRAMA_ID,
                        "  Azul Fidelidade  ",
                        "  Pedro Costa  "
                ))
        );
    }

    public static Stream<Arguments> criarInvalido() {
        return Stream.of(
                Arguments.of(new CriarInvalidoScenario(
                        "should throw when tenantId is null",
                        null,
                        PROGRAMA_ID,
                        "Smiles",
                        "Joao Silva",
                        NullPointerException.class,
                        "tenantId eh obrigatorio"
                )),
                Arguments.of(new CriarInvalidoScenario(
                        "should throw when programaId is null",
                        TENANT_ID,
                        null,
                        "Smiles",
                        "Joao Silva",
                        NullPointerException.class,
                        "programaId eh obrigatorio"
                )),
                Arguments.of(new CriarInvalidoScenario(
                        "should throw when programaNome is null",
                        TENANT_ID,
                        PROGRAMA_ID,
                        null,
                        "Joao Silva",
                        NullPointerException.class,
                        "programaNome eh obrigatorio"
                )),
                Arguments.of(new CriarInvalidoScenario(
                        "should throw when owner is null",
                        TENANT_ID,
                        PROGRAMA_ID,
                        "Smiles",
                        null,
                        NullPointerException.class,
                        "owner eh obrigatorio"
                )),
                Arguments.of(new CriarInvalidoScenario(
                        "should throw when programaNome is blank",
                        TENANT_ID,
                        PROGRAMA_ID,
                        "   ",
                        "Joao Silva",
                        IllegalArgumentException.class,
                        "programaNome nao pode estar vazio"
                )),
                Arguments.of(new CriarInvalidoScenario(
                        "should throw when owner is blank",
                        TENANT_ID,
                        PROGRAMA_ID,
                        "Smiles",
                        "   ",
                        IllegalArgumentException.class,
                        "owner nao pode estar vazio"
                ))
        );
    }

    public static Stream<Arguments> construtorInvalido() {
        return Stream.of(
                Arguments.of(new ConstrutorInvalidoScenario(
                        "should throw when id is null",
                        null, TENANT_ID, PROGRAMA_ID, "Smiles", "Joao",
                        0L, BigDecimal.ZERO, BigDecimal.ZERO, AGORA, AGORA,
                        NullPointerException.class, "id eh obrigatorio"
                )),
                Arguments.of(new ConstrutorInvalidoScenario(
                        "should throw when saldoMilhas is negative",
                        CONTA_ID, TENANT_ID, PROGRAMA_ID, "Smiles", "Joao",
                        -100L, BigDecimal.ZERO, BigDecimal.ZERO, AGORA, AGORA,
                        IllegalArgumentException.class, "saldoMilhas nao pode ser negativo"
                )),
                Arguments.of(new ConstrutorInvalidoScenario(
                        "should throw when custoBaseTotalBRL is negative",
                        CONTA_ID, TENANT_ID, PROGRAMA_ID, "Smiles", "Joao",
                        1000L, new BigDecimal("-100.00"), BigDecimal.ZERO, AGORA, AGORA,
                        IllegalArgumentException.class, "custoBaseTotalBRL nao pode ser negativo"
                )),
                Arguments.of(new ConstrutorInvalidoScenario(
                        "should throw when custoMedioMilheiroAtual is negative",
                        CONTA_ID, TENANT_ID, PROGRAMA_ID, "Smiles", "Joao",
                        1000L, BigDecimal.TEN, new BigDecimal("-5.00"), AGORA, AGORA,
                        IllegalArgumentException.class, "custoMedioMilheiroAtual nao pode ser negativo"
                )),
                Arguments.of(new ConstrutorInvalidoScenario(
                        "should throw when saldo is zero but custoBase is not",
                        CONTA_ID, TENANT_ID, PROGRAMA_ID, "Smiles", "Joao",
                        0L, new BigDecimal("100.00"), BigDecimal.ZERO, AGORA, AGORA,
                        IllegalArgumentException.class, "custoBaseTotalBRL deve ser zero quando saldoMilhas eh zero"
                )),
                Arguments.of(new ConstrutorInvalidoScenario(
                        "should throw when criadoEm is null",
                        CONTA_ID, TENANT_ID, PROGRAMA_ID, "Smiles", "Joao",
                        0L, BigDecimal.ZERO, BigDecimal.ZERO, null, AGORA,
                        NullPointerException.class, "criadoEm eh obrigatorio"
                )),
                Arguments.of(new ConstrutorInvalidoScenario(
                        "should throw when atualizadoEm is null",
                        CONTA_ID, TENANT_ID, PROGRAMA_ID, "Smiles", "Joao",
                        0L, BigDecimal.ZERO, BigDecimal.ZERO, AGORA, null,
                        NullPointerException.class, "atualizadoEm eh obrigatorio"
                ))
        );
    }

    public static Stream<Arguments> aplicarCompra() {
        return Stream.of(
                Arguments.of(new AplicarCompraScenario(
                        "should apply first purchase to empty account",
                        contaZerada(),
                        10_000L,
                        new BigDecimal("250.00"),
                        10_000L,
                        new BigDecimal("250.0000"),
                        new BigDecimal("25.000000")
                )),
                Arguments.of(new AplicarCompraScenario(
                        "should apply purchase to existing balance",
                        contaComSaldo(10_000L, new BigDecimal("250.00"), new BigDecimal("25.00")),
                        5_000L,
                        new BigDecimal("100.00"),
                        15_000L,
                        new BigDecimal("350.0000"),
                        new BigDecimal("23.333333")
                )),
                Arguments.of(new AplicarCompraScenario(
                        "should apply large purchase",
                        contaZerada(),
                        100_000L,
                        new BigDecimal("2500.00"),
                        100_000L,
                        new BigDecimal("2500.0000"),
                        new BigDecimal("25.000000")
                )),
                Arguments.of(new AplicarCompraScenario(
                        "should apply purchase with value zero (free miles)",
                        contaComSaldo(5_000L, new BigDecimal("100.00"), new BigDecimal("20.00")),
                        1_000L,
                        BigDecimal.ZERO,
                        6_000L,
                        new BigDecimal("100.0000"),
                        new BigDecimal("16.666667")
                ))
        );
    }

    public static Stream<Arguments> aplicarCompraInvalido() {
        return Stream.of(
                Arguments.of(new AplicarCompraInvalidoScenario(
                        "should throw when milhas is zero",
                        contaZerada(),
                        0L,
                        new BigDecimal("100.00"),
                        IllegalArgumentException.class,
                        "milhas deve ser positivo para compra"
                )),
                Arguments.of(new AplicarCompraInvalidoScenario(
                        "should throw when milhas is negative",
                        contaZerada(),
                        -1000L,
                        new BigDecimal("100.00"),
                        IllegalArgumentException.class,
                        "milhas deve ser positivo para compra"
                )),
                Arguments.of(new AplicarCompraInvalidoScenario(
                        "should throw when valor is null",
                        contaZerada(),
                        1000L,
                        null,
                        NullPointerException.class,
                        "valor eh obrigatorio"
                )),
                Arguments.of(new AplicarCompraInvalidoScenario(
                        "should throw when valor is negative",
                        contaZerada(),
                        1000L,
                        new BigDecimal("-100.00"),
                        IllegalArgumentException.class,
                        "valor nao pode ser negativo"
                ))
        );
    }

    public static Stream<Arguments> aplicarBonus() {
        return Stream.of(
                Arguments.of(new AplicarBonusScenario(
                        "should apply bonus to empty account (free miles)",
                        contaZerada(),
                        5_000L,
                        5_000L,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO
                )),
                Arguments.of(new AplicarBonusScenario(
                        "should apply bonus to existing balance (reduces custo medio)",
                        contaComSaldo(10_000L, new BigDecimal("250.00"), new BigDecimal("25.00")),
                        5_000L,
                        15_000L,
                        new BigDecimal("250.00"),
                        new BigDecimal("16.666667")
                )),
                Arguments.of(new AplicarBonusScenario(
                        "should apply large bonus",
                        contaComSaldo(10_000L, new BigDecimal("250.00"), new BigDecimal("25.00")),
                        50_000L,
                        60_000L,
                        new BigDecimal("250.00"),
                        new BigDecimal("4.166667")
                ))
        );
    }

    public static Stream<Arguments> aplicarBonusInvalido() {
        return Stream.of(
                Arguments.of(new AplicarBonusInvalidoScenario(
                        "should throw when milhas is zero",
                        contaZerada(),
                        0L,
                        IllegalArgumentException.class,
                        "milhas deve ser positivo para bonus"
                )),
                Arguments.of(new AplicarBonusInvalidoScenario(
                        "should throw when milhas is negative",
                        contaComSaldo(1000L, BigDecimal.TEN, BigDecimal.TEN),
                        -500L,
                        IllegalArgumentException.class,
                        "milhas deve ser positivo para bonus"
                ))
        );
    }

    public static Stream<Arguments> aplicarVenda() {
        return Stream.of(
                Arguments.of(new AplicarVendaScenario(
                        "should apply partial sale with profit",
                        contaComSaldo(10_000L, new BigDecimal("250.00"), new BigDecimal("25.00")),
                        5_000L,
                        new BigDecimal("150.00"),
                        5_000L,
                        new BigDecimal("125.0000"),
                        new BigDecimal("25.000000"),
                        new BigDecimal("125.0000"),
                        new BigDecimal("25.0000")
                )),
                Arguments.of(new AplicarVendaScenario(
                        "should apply sale with loss",
                        contaComSaldo(10_000L, new BigDecimal("250.00"), new BigDecimal("25.00")),
                        5_000L,
                        new BigDecimal("100.00"),
                        5_000L,
                        new BigDecimal("125.0000"),
                        new BigDecimal("25.000000"),
                        new BigDecimal("125.0000"),
                        new BigDecimal("-25.0000")
                )),
                Arguments.of(new AplicarVendaScenario(
                        "should apply full sale zeroing the account",
                        contaComSaldo(10_000L, new BigDecimal("250.00"), new BigDecimal("25.00")),
                        10_000L,
                        new BigDecimal("300.00"),
                        0L,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        new BigDecimal("250.0000"),
                        new BigDecimal("50.0000")
                )),
                Arguments.of(new AplicarVendaScenario(
                        "should apply break-even sale",
                        contaComSaldo(10_000L, new BigDecimal("250.00"), new BigDecimal("25.00")),
                        5_000L,
                        new BigDecimal("125.00"),
                        5_000L,
                        new BigDecimal("125.0000"),
                        new BigDecimal("25.000000"),
                        new BigDecimal("125.0000"),
                        new BigDecimal("0.0000")
                ))
        );
    }

    public static Stream<Arguments> aplicarVendaInvalido() {
        return Stream.of(
                Arguments.of(new AplicarVendaInvalidoScenario(
                        "should throw when milhas is zero",
                        contaComSaldo(10_000L, new BigDecimal("250.00"), new BigDecimal("25.00")),
                        0L,
                        new BigDecimal("100.00"),
                        IllegalArgumentException.class,
                        "milhas deve ser positivo para venda"
                )),
                Arguments.of(new AplicarVendaInvalidoScenario(
                        "should throw when milhas is negative",
                        contaComSaldo(10_000L, new BigDecimal("250.00"), new BigDecimal("25.00")),
                        -1000L,
                        new BigDecimal("100.00"),
                        IllegalArgumentException.class,
                        "milhas deve ser positivo para venda"
                )),
                Arguments.of(new AplicarVendaInvalidoScenario(
                        "should throw when valorVenda is null",
                        contaComSaldo(10_000L, new BigDecimal("250.00"), new BigDecimal("25.00")),
                        1000L,
                        null,
                        NullPointerException.class,
                        "valorVenda eh obrigatorio"
                )),
                Arguments.of(new AplicarVendaInvalidoScenario(
                        "should throw when valorVenda is negative",
                        contaComSaldo(10_000L, new BigDecimal("250.00"), new BigDecimal("25.00")),
                        1000L,
                        new BigDecimal("-50.00"),
                        IllegalArgumentException.class,
                        "valorVenda nao pode ser negativo"
                )),
                Arguments.of(new AplicarVendaInvalidoScenario(
                        "should throw when selling more than balance",
                        contaComSaldo(5_000L, new BigDecimal("125.00"), new BigDecimal("25.00")),
                        10_000L,
                        new BigDecimal("250.00"),
                        SaldoMilhasInsuficienteException.class,
                        "Saldo de milhas insuficiente no programa " + PROGRAMA_ID
                            + ": saldo atual = 5000, solicitado = 10000"
                )),
                Arguments.of(new AplicarVendaInvalidoScenario(
                        "should throw when selling from empty account",
                        contaZerada(),
                        1000L,
                        new BigDecimal("25.00"),
                        SaldoMilhasInsuficienteException.class,
                        "Saldo de milhas insuficiente no programa " + PROGRAMA_ID
                            + ": saldo atual = 0, solicitado = 1000"
                ))
        );
    }

    public static Stream<Arguments> temSaldo() {
        return Stream.of(
                Arguments.of(new TemSaldoScenario(
                        "should return false for empty account",
                        contaZerada(),
                        false
                )),
                Arguments.of(new TemSaldoScenario(
                        "should return true for account with balance",
                        contaComSaldo(1_000L, new BigDecimal("25.00"), new BigDecimal("25.00")),
                        true
                )),
                Arguments.of(new TemSaldoScenario(
                        "should return true for account with large balance",
                        contaComSaldo(100_000L, new BigDecimal("2500.00"), new BigDecimal("25.00")),
                        true
                ))
        );
    }

    public static Stream<Arguments> podeSacar() {
        return Stream.of(
                Arguments.of(new PodeSacarScenario(
                        "should return false when withdrawing from empty account",
                        contaZerada(),
                        1_000L,
                        false
                )),
                Arguments.of(new PodeSacarScenario(
                        "should return true when withdrawing within balance",
                        contaComSaldo(10_000L, new BigDecimal("250.00"), new BigDecimal("25.00")),
                        5_000L,
                        true
                )),
                Arguments.of(new PodeSacarScenario(
                        "should return true when withdrawing exact balance",
                        contaComSaldo(10_000L, new BigDecimal("250.00"), new BigDecimal("25.00")),
                        10_000L,
                        true
                )),
                Arguments.of(new PodeSacarScenario(
                        "should return false when withdrawing more than balance",
                        contaComSaldo(5_000L, new BigDecimal("125.00"), new BigDecimal("25.00")),
                        10_000L,
                        false
                )),
                Arguments.of(new PodeSacarScenario(
                        "should return false when withdrawing zero",
                        contaComSaldo(10_000L, new BigDecimal("250.00"), new BigDecimal("25.00")),
                        0L,
                        false
                )),
                Arguments.of(new PodeSacarScenario(
                        "should return false when withdrawing negative",
                        contaComSaldo(10_000L, new BigDecimal("250.00"), new BigDecimal("25.00")),
                        -1000L,
                        false
                ))
        );
    }

    public static Stream<Arguments> fluxoCompleto() {
        return Stream.of(
                Arguments.of(new FluxoCompletoScenario(
                        "should handle complete flow: compra + bonus + venda with profit",
                        TENANT_ID,
                        PROGRAMA_ID,
                        "Smiles",
                        "Joao Silva",
                        10_000L,
                        new BigDecimal("250.00"),
                        5_000L,
                        5_000L,
                        new BigDecimal("100.00"),
                        10_000L,
                        new BigDecimal("16.67")
                )),
                Arguments.of(new FluxoCompletoScenario(
                        "should handle complete flow with full sale",
                        TENANT_ID_2,
                        PROGRAMA_ID_2,
                        "LATAM Pass",
                        "Maria Santos",
                        20_000L,
                        new BigDecimal("400.00"),
                        0L,
                        20_000L,
                        new BigDecimal("500.00"),
                        0L,
                        new BigDecimal("100.00")
                ))
        );
    }
}
