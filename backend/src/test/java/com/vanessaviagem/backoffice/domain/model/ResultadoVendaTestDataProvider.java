package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.ResultadoVendaScenario.CriacaoValidaScenario;
import com.vanessaviagem.backoffice.domain.model.ResultadoVendaScenario.LucroOuPrejuizoScenario;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

/**
 * Test data provider for ResultadoVenda tests.
 */
public final class ResultadoVendaTestDataProvider {

    private static final UUID TENANT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID PROGRAMA_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID CONTA_ID = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
    private static final LocalDateTime AGORA = LocalDateTime.of(2025, 1, 15, 10, 30, 0);

    private ResultadoVendaTestDataProvider() {
    }

    private static ContaPrograma contaPadrao() {
        return new ContaPrograma(
                CONTA_ID,
                TENANT_ID,
                PROGRAMA_ID,
                "Smiles",
                "Joao Silva",
                5_000L,
                new BigDecimal("125.00"),
                new BigDecimal("25.00"),
                AGORA,
                AGORA
        );
    }

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

    public static Stream<Arguments> criacaoValida() {
        return Stream.of(
                Arguments.of(new CriacaoValidaScenario(
                        "should create ResultadoVenda with profit",
                        contaPadrao(),
                        new BigDecimal("125.00"),
                        new BigDecimal("25.00")
                )),
                Arguments.of(new CriacaoValidaScenario(
                        "should create ResultadoVenda with loss",
                        contaPadrao(),
                        new BigDecimal("125.00"),
                        new BigDecimal("-25.00")
                )),
                Arguments.of(new CriacaoValidaScenario(
                        "should create ResultadoVenda with break-even",
                        contaPadrao(),
                        new BigDecimal("125.00"),
                        BigDecimal.ZERO
                )),
                Arguments.of(new CriacaoValidaScenario(
                        "should create ResultadoVenda when selling all miles",
                        contaZerada(),
                        new BigDecimal("300.00"),
                        new BigDecimal("100.00")
                ))
        );
    }

    public static Stream<Arguments> lucroOuPrejuizo() {
        return Stream.of(
                Arguments.of(new LucroOuPrejuizoScenario(
                        "should identify profit for positive lucro",
                        new BigDecimal("25.00"),
                        true,
                        false
                )),
                Arguments.of(new LucroOuPrejuizoScenario(
                        "should identify loss for negative lucro",
                        new BigDecimal("-25.00"),
                        false,
                        true
                )),
                Arguments.of(new LucroOuPrejuizoScenario(
                        "should identify break-even for zero lucro",
                        BigDecimal.ZERO,
                        false,
                        false
                )),
                Arguments.of(new LucroOuPrejuizoScenario(
                        "should handle small positive profit",
                        new BigDecimal("0.01"),
                        true,
                        false
                )),
                Arguments.of(new LucroOuPrejuizoScenario(
                        "should handle small negative loss",
                        new BigDecimal("-0.01"),
                        false,
                        true
                ))
        );
    }
}
