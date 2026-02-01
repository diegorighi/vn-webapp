package com.vanessaviagem.backoffice.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhasScenario.AtivarScenario;
import com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhasScenario.ComRegrasArredondamentoScenario;
import com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhasScenario.CriacaoInvalidaScenario;
import com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhasScenario.CriacaoValidaScenario;
import com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhasScenario.DesativarScenario;
import com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhasScenario.FactoryCriarComMoedaScenario;
import com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhasScenario.FactoryCriarScenario;
import com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhasScenario.IsAtivoScenario;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for {@link ProgramaDeMilhas} domain entity.
 */
class ProgramaDeMilhasTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhasTestDataProvider#criacaoValida")
    void shouldCreateProgramaWithValidData(CriacaoValidaScenario scenario) {
        ProgramaDeMilhas programa = new ProgramaDeMilhas(
                scenario.id(),
                scenario.brand(),
                scenario.status(),
                scenario.moeda(),
                scenario.regrasArredondamento()
        );

        assertThat(programa.id()).isEqualTo(scenario.id());
        assertThat(programa.brand()).isEqualTo(scenario.brand());
        assertThat(programa.status()).isEqualTo(scenario.status());
        assertThat(programa.moeda()).isEqualTo(scenario.moeda());
        assertThat(programa.regrasArredondamento()).isEqualTo(scenario.regrasArredondamento());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhasTestDataProvider#criacaoInvalida")
    void shouldThrowWhenCreatingProgramaWithInvalidData(CriacaoInvalidaScenario scenario) {
        assertThatThrownBy(() -> new ProgramaDeMilhas(
                scenario.id(),
                scenario.brand(),
                scenario.status(),
                scenario.moeda(),
                scenario.regrasArredondamento()
        ))
                .isInstanceOf(scenario.expectedException())
                .hasMessage(scenario.expectedMessage());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhasTestDataProvider#factoryCriar")
    void shouldCreateProgramaUsingCriarFactory(FactoryCriarScenario scenario) {
        ProgramaDeMilhas programa = ProgramaDeMilhas.criar(scenario.id(), scenario.brand());

        assertThat(programa.id()).isEqualTo(scenario.id());
        assertThat(programa.brand()).isEqualTo(scenario.brand());
        assertThat(programa.status()).isEqualTo(scenario.expectedStatus());
        assertThat(programa.moeda()).isEqualTo(scenario.expectedMoeda());
        assertThat(programa.regrasArredondamento()).isEqualTo(ConfigArredondamento.DEFAULT);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhasTestDataProvider#factoryCriarComMoeda")
    void shouldCreateProgramaUsingCriarComMoedaFactory(FactoryCriarComMoedaScenario scenario) {
        ProgramaDeMilhas programa = ProgramaDeMilhas.criarComMoeda(
                scenario.id(),
                scenario.brand(),
                scenario.moeda()
        );

        assertThat(programa.id()).isEqualTo(scenario.id());
        assertThat(programa.brand()).isEqualTo(scenario.brand());
        assertThat(programa.status()).isEqualTo(scenario.expectedStatus());
        assertThat(programa.moeda()).isEqualTo(scenario.moeda());
        assertThat(programa.regrasArredondamento()).isEqualTo(ConfigArredondamento.DEFAULT);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhasTestDataProvider#isAtivo")
    void shouldCheckIfProgramaIsAtivo(IsAtivoScenario scenario) {
        boolean result = scenario.programa().isAtivo();

        assertThat(result).isEqualTo(scenario.expectedAtivo());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhasTestDataProvider#desativar")
    void shouldDesativarPrograma(DesativarScenario scenario) {
        ProgramaDeMilhas desativado = scenario.programaOriginal().desativar();

        assertThat(desativado.status()).isEqualTo(scenario.expectedStatus());
        assertThat(desativado.id()).isEqualTo(scenario.programaOriginal().id());
        assertThat(desativado.brand()).isEqualTo(scenario.programaOriginal().brand());
        assertThat(desativado.moeda()).isEqualTo(scenario.programaOriginal().moeda());
        assertThat(desativado.regrasArredondamento()).isEqualTo(scenario.programaOriginal().regrasArredondamento());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhasTestDataProvider#ativar")
    void shouldAtivarPrograma(AtivarScenario scenario) {
        ProgramaDeMilhas ativado = scenario.programaOriginal().ativar();

        assertThat(ativado.status()).isEqualTo(scenario.expectedStatus());
        assertThat(ativado.id()).isEqualTo(scenario.programaOriginal().id());
        assertThat(ativado.brand()).isEqualTo(scenario.programaOriginal().brand());
        assertThat(ativado.moeda()).isEqualTo(scenario.programaOriginal().moeda());
        assertThat(ativado.regrasArredondamento()).isEqualTo(scenario.programaOriginal().regrasArredondamento());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhasTestDataProvider#comRegrasArredondamento")
    void shouldUpdateRegrasArredondamento(ComRegrasArredondamentoScenario scenario) {
        ProgramaDeMilhas atualizado = scenario.programaOriginal()
                .comRegrasArredondamento(scenario.novaConfig());

        assertThat(atualizado.regrasArredondamento()).isEqualTo(scenario.novaConfig());
        assertThat(atualizado.id()).isEqualTo(scenario.programaOriginal().id());
        assertThat(atualizado.brand()).isEqualTo(scenario.programaOriginal().brand());
        assertThat(atualizado.status()).isEqualTo(scenario.programaOriginal().status());
        assertThat(atualizado.moeda()).isEqualTo(scenario.programaOriginal().moeda());
    }
}
