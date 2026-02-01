package com.vanessaviagem.backoffice.domain.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import com.vanessaviagem.backoffice.domain.exceptions.MilhasNaoEncontradaExceptionScenario.CriacaoComIdScenario;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for {@link MilhasNaoEncontradaException}.
 */
class MilhasNaoEncontradaExceptionTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.exceptions.MilhasNaoEncontradaExceptionTestDataProvider#criacaoComId")
    void shouldCreateExceptionWithMilhasId(CriacaoComIdScenario scenario) {
        MilhasNaoEncontradaException exception = new MilhasNaoEncontradaException(scenario.milhasId());

        assertThat(exception.getMessage()).isEqualTo(scenario.expectedMessage());
        assertThat(exception.getMilhasId()).isEqualTo(scenario.milhasId());
        assertThat(exception).isInstanceOf(DomainException.class);
    }
}
