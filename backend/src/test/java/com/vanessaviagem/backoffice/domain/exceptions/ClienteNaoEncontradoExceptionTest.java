package com.vanessaviagem.backoffice.domain.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ClienteNaoEncontradoExceptionTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.exceptions.ClienteNaoEncontradoExceptionTestDataProvider#criacaoComId")
    void shouldCreateExceptionWithClienteId(ClienteNaoEncontradoExceptionScenario scenario) {
        ClienteNaoEncontradoException exception = new ClienteNaoEncontradoException(scenario.clienteId());

        assertThat(exception.getMessage()).isEqualTo(scenario.expectedMessagePattern());
        assertThat(exception.getClienteId()).isEqualTo(scenario.clienteId());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.exceptions.ClienteNaoEncontradoExceptionTestDataProvider#criacaoComIdEMensagem")
    void shouldCreateExceptionWithClienteIdAndCustomMessage(ClienteNaoEncontradoExceptionScenario scenario) {
        ClienteNaoEncontradoException exception = new ClienteNaoEncontradoException(
                scenario.clienteId(),
                scenario.customMessage()
        );

        assertThat(exception.getMessage()).isEqualTo(scenario.expectedMessagePattern());
        assertThat(exception.getClienteId()).isEqualTo(scenario.clienteId());
    }
}
