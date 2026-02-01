package com.vanessaviagem.backoffice.domain.model.cliente;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ClienteTitularTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.cliente.ClienteTitularTestDataProvider#criacaoValida")
    void shouldCreateClienteTitularWithValidData(ClienteTitularScenario scenario) {
        ClienteTitular cliente = new ClienteTitular(
                scenario.clienteId(),
                scenario.dadosPessoais(),
                scenario.viagens(),
                scenario.ativo(),
                scenario.dependentes()
        );

        assertThat(cliente.clienteId()).isEqualTo(scenario.clienteId());
        assertThat(cliente.dadosPessoais()).isEqualTo(scenario.dadosPessoais());
        assertThat(cliente.viagens()).isEqualTo(scenario.viagens());
        assertThat(cliente.ativo()).isEqualTo(scenario.ativo());
        assertThat(cliente.dependentes()).isEqualTo(scenario.dependentes());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.cliente.ClienteTitularTestDataProvider#validacaoNulos")
    void shouldThrowWhenRequiredFieldIsNull(ClienteTitularScenario scenario) {
        assertThatThrownBy(() -> new ClienteTitular(
                scenario.clienteId(),
                scenario.dadosPessoais(),
                scenario.viagens(),
                scenario.ativo(),
                scenario.dependentes()
        ))
                .isInstanceOf(scenario.expectedException())
                .hasMessage(scenario.expectedMessage());
    }
}
