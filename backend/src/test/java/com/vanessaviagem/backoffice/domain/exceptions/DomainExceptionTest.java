package com.vanessaviagem.backoffice.domain.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class DomainExceptionTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.exceptions.DomainExceptionTestDataProvider#criacaoComMensagem")
    void shouldCreateExceptionWithMessage(DomainExceptionScenario scenario) {
        DomainException exception = new DomainException(scenario.message());

        assertThat(exception.getMessage()).isEqualTo(scenario.expectedMessage());
        assertThat(exception.getCause()).isNull();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.exceptions.DomainExceptionTestDataProvider#criacaoComMensagemECausa")
    void shouldCreateExceptionWithMessageAndCause(DomainExceptionScenario scenario) {
        DomainException exception = new DomainException(scenario.message(), scenario.cause());

        assertThat(exception.getMessage()).isEqualTo(scenario.expectedMessage());
        assertThat(exception.getCause()).isEqualTo(scenario.cause());
    }
}
