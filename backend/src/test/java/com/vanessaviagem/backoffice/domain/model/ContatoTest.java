package com.vanessaviagem.backoffice.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ContatoTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.ContatoTestDataProvider#criacaoValida")
    void shouldCreateContatoWithValidData(ContatoScenario scenario) {
        Contato contato = new Contato(
                scenario.principal(),
                scenario.tipo(),
                scenario.valor()
        );

        assertThat(contato.principal()).isEqualTo(scenario.principal());
        assertThat(contato.tipo()).isEqualTo(scenario.tipo());
        assertThat(contato.valor()).isEqualTo(scenario.valor());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.ContatoTestDataProvider#validacaoNulos")
    void shouldThrowWhenRequiredFieldIsNull(ContatoScenario scenario) {
        assertThatThrownBy(() -> new Contato(
                scenario.principal(),
                scenario.tipo(),
                scenario.valor()
        ))
                .isInstanceOf(scenario.expectedException())
                .hasMessage(scenario.expectedMessage());
    }
}
