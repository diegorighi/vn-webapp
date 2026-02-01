package com.vanessaviagem.backoffice.application.ports.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.vanessaviagem.backoffice.application.ports.in.RegistrarClienteUseCase.RegistrarClienteCommand;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarClienteUseCase.RegistrarClienteResult;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarClienteUseCaseScenario.CommandInvalidScenario;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarClienteUseCaseScenario.CommandValidScenario;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarClienteUseCaseScenario.ResultInvalidScenario;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarClienteUseCaseScenario.ResultValidScenario;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class RegistrarClienteUseCaseTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.application.ports.in.RegistrarClienteUseCaseTestDataProvider#commandValido")
    void shouldCreateCommandWithValidData(CommandValidScenario scenario) {
        RegistrarClienteCommand command = new RegistrarClienteCommand(scenario.dadosPessoais());

        assertThat(command.dadosPessoais()).isEqualTo(scenario.dadosPessoais());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.application.ports.in.RegistrarClienteUseCaseTestDataProvider#commandInvalido")
    void shouldThrowWhenCommandHasInvalidData(CommandInvalidScenario scenario) {
        assertThatThrownBy(() -> new RegistrarClienteCommand(scenario.dadosPessoais()))
                .isInstanceOf(scenario.expectedException())
                .hasMessage(scenario.expectedMessage());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.application.ports.in.RegistrarClienteUseCaseTestDataProvider#resultValido")
    void shouldCreateResultWithValidData(ResultValidScenario scenario) {
        RegistrarClienteResult result = new RegistrarClienteResult(
                scenario.clienteId(),
                scenario.cliente()
        );

        assertThat(result.clienteId()).isEqualTo(scenario.clienteId());
        assertThat(result.cliente()).isEqualTo(scenario.cliente());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.application.ports.in.RegistrarClienteUseCaseTestDataProvider#resultInvalido")
    void shouldThrowWhenResultHasInvalidData(ResultInvalidScenario scenario) {
        assertThatThrownBy(() -> new RegistrarClienteResult(
                scenario.clienteId(),
                scenario.cliente()
        ))
                .isInstanceOf(scenario.expectedException())
                .hasMessage(scenario.expectedMessage());
    }
}
