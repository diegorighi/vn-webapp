package com.vanessaviagem.backoffice.domain.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import com.vanessaviagem.backoffice.domain.exceptions.ViagemNaoEncontradaExceptionScenario.CriacaoComIdEMensagemScenario;
import com.vanessaviagem.backoffice.domain.exceptions.ViagemNaoEncontradaExceptionScenario.CriacaoComIdScenario;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for {@link ViagemNaoEncontradaException}.
 */
class ViagemNaoEncontradaExceptionTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.exceptions.ViagemNaoEncontradaExceptionTestDataProvider#criacaoComId")
    void shouldCreateExceptionWithViagemId(CriacaoComIdScenario scenario) {
        ViagemNaoEncontradaException exception = new ViagemNaoEncontradaException(scenario.viagemId());

        assertThat(exception.getMessage()).isEqualTo(scenario.expectedMessage());
        assertThat(exception.getViagemId()).isEqualTo(scenario.viagemId());
        assertThat(exception).isInstanceOf(DomainException.class);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.exceptions.ViagemNaoEncontradaExceptionTestDataProvider#criacaoComIdEMensagem")
    void shouldCreateExceptionWithViagemIdAndCustomMessage(CriacaoComIdEMensagemScenario scenario) {
        ViagemNaoEncontradaException exception = new ViagemNaoEncontradaException(
                scenario.viagemId(),
                scenario.customMessage()
        );

        assertThat(exception.getMessage()).isEqualTo(scenario.expectedMessage());
        assertThat(exception.getViagemId()).isEqualTo(scenario.viagemId());
        assertThat(exception).isInstanceOf(DomainException.class);
    }
}
