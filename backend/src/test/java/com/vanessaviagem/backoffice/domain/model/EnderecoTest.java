package com.vanessaviagem.backoffice.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class EnderecoTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.EnderecoTestDataProvider#criacaoValida")
    void shouldCreateEnderecoWithValidData(EnderecoScenario scenario) {
        Endereco endereco = new Endereco(
                scenario.principal(),
                scenario.logradouro(),
                scenario.numero(),
                scenario.bairro(),
                scenario.cep(),
                scenario.cidade(),
                scenario.estado(),
                scenario.pais()
        );

        assertThat(endereco.principal()).isEqualTo(scenario.principal());
        assertThat(endereco.logradouro()).isEqualTo(scenario.logradouro());
        assertThat(endereco.numero()).isEqualTo(scenario.numero());
        assertThat(endereco.bairro()).isEqualTo(scenario.bairro());
        assertThat(endereco.cep()).isEqualTo(scenario.cep());
        assertThat(endereco.cidade()).isEqualTo(scenario.cidade());
        assertThat(endereco.estado()).isEqualTo(scenario.estado());
        assertThat(endereco.pais()).isEqualTo(scenario.pais());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.EnderecoTestDataProvider#validacaoNulos")
    void shouldThrowWhenRequiredFieldIsNull(EnderecoScenario scenario) {
        assertThatThrownBy(() -> new Endereco(
                scenario.principal(),
                scenario.logradouro(),
                scenario.numero(),
                scenario.bairro(),
                scenario.cep(),
                scenario.cidade(),
                scenario.estado(),
                scenario.pais()
        ))
                .isInstanceOf(scenario.expectedException())
                .hasMessage(scenario.expectedMessage());
    }
}
