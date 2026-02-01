package com.vanessaviagem.backoffice.domain.model.cliente;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ClienteDependenteTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.cliente.ClienteDependenteTestDataProvider#criacaoValida")
    void shouldCreateClienteDependenteWithValidData(ClienteDependenteScenario scenario) {
        ClienteDependente cliente = new ClienteDependente(
                scenario.clienteId(),
                scenario.titularId(),
                scenario.parentesco(),
                scenario.dadosPessoais(),
                scenario.viagens(),
                scenario.ativo()
        );

        assertThat(cliente.clienteId()).isEqualTo(scenario.clienteId());
        assertThat(cliente.titularId()).isEqualTo(scenario.titularId());
        assertThat(cliente.parentesco()).isEqualTo(scenario.parentesco());
        assertThat(cliente.dadosPessoais()).isEqualTo(scenario.dadosPessoais());
        assertThat(cliente.viagens()).isEqualTo(scenario.viagens());
        assertThat(cliente.ativo()).isEqualTo(scenario.ativo());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.cliente.ClienteDependenteTestDataProvider#validacaoNulos")
    void shouldThrowWhenRequiredFieldIsNull(ClienteDependenteScenario scenario) {
        assertThatThrownBy(() -> new ClienteDependente(
                scenario.clienteId(),
                scenario.titularId(),
                scenario.parentesco(),
                scenario.dadosPessoais(),
                scenario.viagens(),
                scenario.ativo()
        ))
                .isInstanceOf(scenario.expectedException())
                .hasMessage(scenario.expectedMessage());
    }
}
