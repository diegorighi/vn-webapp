package com.vanessaviagem.backoffice.domain.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class SaldoMilhasInsuficienteExceptionTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.exceptions.SaldoMilhasInsuficienteExceptionTestDataProvider#cenarios")
    void shouldCreateExceptionAndCalculateDeficit(SaldoMilhasInsuficienteExceptionScenario scenario) {
        SaldoMilhasInsuficienteException exception = new SaldoMilhasInsuficienteException(
                scenario.programaId(),
                scenario.saldoAtual(),
                scenario.milhasSolicitadas()
        );

        assertThat(exception.getProgramaId()).isEqualTo(scenario.programaId());
        assertThat(exception.getSaldoAtual()).isEqualTo(scenario.saldoAtual());
        assertThat(exception.getMilhasSolicitadas()).isEqualTo(scenario.milhasSolicitadas());
        assertThat(exception.getDeficit()).isEqualTo(scenario.expectedDeficit());
        assertThat(exception.getMessage()).contains(scenario.programaId().toString());
        assertThat(exception.getMessage()).contains(String.valueOf(scenario.saldoAtual()));
        assertThat(exception.getMessage()).contains(String.valueOf(scenario.milhasSolicitadas()));
    }
}
