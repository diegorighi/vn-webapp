package com.vanessaviagem.backoffice.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.vanessaviagem.backoffice.domain.model.ResultadoVendaScenario.CriacaoValidaScenario;
import com.vanessaviagem.backoffice.domain.model.ResultadoVendaScenario.LucroOuPrejuizoScenario;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for {@link ResultadoVenda} value object.
 *
 * <p>ResultadoVenda is a simple record with three fields:
 * - contaAtualizada: the updated account after the sale
 * - custoRemovido: the proportional cost removed from the account
 * - lucro: the profit (or loss if negative) from the sale
 */
@DisplayName("ResultadoVenda")
class ResultadoVendaTest {

    private static final String PROVIDER = "com.vanessaviagem.backoffice.domain.model.ResultadoVendaTestDataProvider#";

    private static final UUID TENANT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID PROGRAMA_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID CONTA_ID = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
    private static final LocalDateTime AGORA = LocalDateTime.of(2025, 1, 15, 10, 30, 0);

    private ContaPrograma contaPadrao() {
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

    @ParameterizedTest(name = "{0}")
    @MethodSource(PROVIDER + "criacaoValida")
    @DisplayName("should create ResultadoVenda with valid data")
    void shouldCreateResultadoVendaWithValidData(CriacaoValidaScenario scenario) {
        ResultadoVenda resultado = new ResultadoVenda(
                scenario.contaAtualizada(),
                scenario.custoRemovido(),
                scenario.lucro()
        );

        assertThat(resultado.contaAtualizada()).isEqualTo(scenario.contaAtualizada());
        assertThat(resultado.custoRemovido()).isEqualByComparingTo(scenario.custoRemovido());
        assertThat(resultado.lucro()).isEqualByComparingTo(scenario.lucro());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(PROVIDER + "lucroOuPrejuizo")
    @DisplayName("should correctly identify profit or loss")
    void shouldIdentifyProfitOrLoss(LucroOuPrejuizoScenario scenario) {
        ResultadoVenda resultado = new ResultadoVenda(
                contaPadrao(),
                new BigDecimal("125.00"),
                scenario.lucro()
        );

        // Check profit
        boolean teveLucro = resultado.lucro().compareTo(BigDecimal.ZERO) > 0;
        assertThat(teveLucro).isEqualTo(scenario.teveLucro());

        // Check loss
        boolean tevePrejuizo = resultado.lucro().compareTo(BigDecimal.ZERO) < 0;
        assertThat(tevePrejuizo).isEqualTo(scenario.tevePrejuizo());
    }
}
