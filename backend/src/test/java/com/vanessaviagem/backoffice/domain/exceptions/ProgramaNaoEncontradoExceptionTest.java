package com.vanessaviagem.backoffice.domain.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import com.vanessaviagem.backoffice.domain.exceptions.ProgramaNaoEncontradoExceptionScenario.CriacaoComBrandScenario;
import com.vanessaviagem.backoffice.domain.exceptions.ProgramaNaoEncontradoExceptionScenario.CriacaoComIdEMensagemScenario;
import com.vanessaviagem.backoffice.domain.exceptions.ProgramaNaoEncontradoExceptionScenario.CriacaoComIdScenario;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for {@link ProgramaNaoEncontradoException}.
 */
class ProgramaNaoEncontradoExceptionTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.exceptions.ProgramaNaoEncontradoExceptionTestDataProvider#criacaoComId")
    void shouldCreateExceptionWithProgramaId(CriacaoComIdScenario scenario) {
        ProgramaNaoEncontradoException exception = new ProgramaNaoEncontradoException(scenario.programaId());

        assertThat(exception.getMessage()).isEqualTo(scenario.expectedMessage());
        assertThat(exception.getProgramaId()).isEqualTo(scenario.programaId());
        assertThat(exception.getBrand()).isNull();
        assertThat(exception).isInstanceOf(DomainException.class);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.exceptions.ProgramaNaoEncontradoExceptionTestDataProvider#criacaoComBrand")
    void shouldCreateExceptionWithBrand(CriacaoComBrandScenario scenario) {
        ProgramaNaoEncontradoException exception = new ProgramaNaoEncontradoException(scenario.brand());

        assertThat(exception.getMessage()).isEqualTo(scenario.expectedMessage());
        assertThat(exception.getBrand()).isEqualTo(scenario.brand());
        assertThat(exception.getProgramaId()).isNull();
        assertThat(exception).isInstanceOf(DomainException.class);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.exceptions.ProgramaNaoEncontradoExceptionTestDataProvider#criacaoComIdEMensagem")
    void shouldCreateExceptionWithProgramaIdAndCustomMessage(CriacaoComIdEMensagemScenario scenario) {
        ProgramaNaoEncontradoException exception = new ProgramaNaoEncontradoException(
                scenario.programaId(),
                scenario.customMessage()
        );

        assertThat(exception.getMessage()).isEqualTo(scenario.expectedMessage());
        assertThat(exception.getProgramaId()).isEqualTo(scenario.programaId());
        assertThat(exception.getBrand()).isNull();
        assertThat(exception).isInstanceOf(DomainException.class);
    }
}
