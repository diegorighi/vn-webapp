package com.vanessaviagem.backoffice.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.vanessaviagem.backoffice.domain.model.ConfigArredondamentoScenario.ComCasasDecimaisScenario;
import com.vanessaviagem.backoffice.domain.model.ConfigArredondamentoScenario.CriacaoInvalidaScenario;
import com.vanessaviagem.backoffice.domain.model.ConfigArredondamentoScenario.CriacaoValidaScenario;
import com.vanessaviagem.backoffice.domain.model.ConfigArredondamentoScenario.DefaultConstantScenario;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for {@link ConfigArredondamento} value object.
 */
class ConfigArredondamentoTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.ConfigArredondamentoTestDataProvider#criacaoValida")
    void shouldCreateConfigWithValidData(CriacaoValidaScenario scenario) {
        ConfigArredondamento config = new ConfigArredondamento(
                scenario.casasDecimais(),
                scenario.modoArredondamento()
        );

        assertThat(config.casasDecimais()).isEqualTo(scenario.casasDecimais());
        assertThat(config.modoArredondamento()).isEqualTo(scenario.modoArredondamento());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.ConfigArredondamentoTestDataProvider#criacaoInvalida")
    void shouldThrowWhenCreatingConfigWithInvalidData(CriacaoInvalidaScenario scenario) {
        assertThatThrownBy(() -> new ConfigArredondamento(
                scenario.casasDecimais(),
                scenario.modoArredondamento()
        ))
                .isInstanceOf(scenario.expectedException())
                .hasMessage(scenario.expectedMessage());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.ConfigArredondamentoTestDataProvider#comCasasDecimais")
    void shouldCreateConfigUsingComCasasDecimaisFactory(ComCasasDecimaisScenario scenario) {
        ConfigArredondamento config = ConfigArredondamento.comCasasDecimais(scenario.casasDecimais());

        assertThat(config.casasDecimais()).isEqualTo(scenario.casasDecimais());
        assertThat(config.modoArredondamento()).isEqualTo(scenario.expectedRoundingMode());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.ConfigArredondamentoTestDataProvider#defaultConstant")
    void shouldHaveCorrectDefaultConstant(DefaultConstantScenario scenario) {
        ConfigArredondamento defaultConfig = ConfigArredondamento.DEFAULT;

        assertThat(defaultConfig.casasDecimais()).isEqualTo(scenario.expectedCasasDecimais());
        assertThat(defaultConfig.modoArredondamento()).isEqualTo(scenario.expectedModoArredondamento());
    }
}
