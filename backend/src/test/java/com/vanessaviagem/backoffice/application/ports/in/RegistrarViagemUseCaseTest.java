package com.vanessaviagem.backoffice.application.ports.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.vanessaviagem.backoffice.application.ports.in.RegistrarViagemUseCase.RegistrarViagemCommand;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarViagemUseCase.RegistrarViagemResult;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarViagemUseCaseScenario.CommandInvalidScenario;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarViagemUseCaseScenario.CommandValidScenario;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarViagemUseCaseScenario.ResultInvalidScenario;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarViagemUseCaseScenario.ResultValidScenario;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class RegistrarViagemUseCaseTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.application.ports.in.RegistrarViagemUseCaseTestDataProvider#commandValido")
    void shouldCreateCommandWithValidData(CommandValidScenario scenario) {
        RegistrarViagemCommand command = new RegistrarViagemCommand(
                scenario.clienteId(),
                scenario.localizador(),
                scenario.trecho(),
                scenario.data(),
                scenario.assento(),
                scenario.companhiaAereaList(),
                scenario.moeda(),
                scenario.precoTotal()
        );

        assertThat(command.clienteId()).isEqualTo(scenario.clienteId());
        assertThat(command.localizador()).isEqualTo(scenario.localizador());
        assertThat(command.trecho()).isEqualTo(scenario.trecho());
        assertThat(command.data()).isEqualTo(scenario.data());
        assertThat(command.assento()).isEqualTo(scenario.assento());
        assertThat(command.companhiaAereaList()).isEqualTo(scenario.companhiaAereaList());
        assertThat(command.moeda()).isEqualTo(scenario.moeda());
        assertThat(command.precoTotal()).isEqualTo(scenario.precoTotal());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.application.ports.in.RegistrarViagemUseCaseTestDataProvider#commandInvalido")
    void shouldThrowWhenCommandHasInvalidData(CommandInvalidScenario scenario) {
        assertThatThrownBy(() -> new RegistrarViagemCommand(
                scenario.clienteId(),
                scenario.localizador(),
                scenario.trecho(),
                scenario.data(),
                scenario.assento(),
                scenario.companhiaAereaList(),
                scenario.moeda(),
                scenario.precoTotal()
        ))
                .isInstanceOf(scenario.expectedException())
                .hasMessage(scenario.expectedMessage());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.application.ports.in.RegistrarViagemUseCaseTestDataProvider#resultValido")
    void shouldCreateResultWithValidData(ResultValidScenario scenario) {
        RegistrarViagemResult result = new RegistrarViagemResult(
                scenario.viagemId(),
                scenario.viagem()
        );

        assertThat(result.viagemId()).isEqualTo(scenario.viagemId());
        assertThat(result.viagem()).isEqualTo(scenario.viagem());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.application.ports.in.RegistrarViagemUseCaseTestDataProvider#resultInvalido")
    void shouldThrowWhenResultHasInvalidData(ResultInvalidScenario scenario) {
        assertThatThrownBy(() -> new RegistrarViagemResult(
                scenario.viagemId(),
                scenario.viagem()
        ))
                .isInstanceOf(scenario.expectedException())
                .hasMessage(scenario.expectedMessage());
    }
}
