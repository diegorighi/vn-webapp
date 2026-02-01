package com.vanessaviagem.backoffice.application.ports.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.vanessaviagem.backoffice.application.ports.in.AdicionarDependenteUseCase.AdicionarDependenteCommand;
import com.vanessaviagem.backoffice.application.ports.in.AdicionarDependenteUseCase.AdicionarDependenteResult;
import com.vanessaviagem.backoffice.application.ports.in.AdicionarDependenteUseCaseScenario.CommandInvalidScenario;
import com.vanessaviagem.backoffice.application.ports.in.AdicionarDependenteUseCaseScenario.CommandValidScenario;
import com.vanessaviagem.backoffice.application.ports.in.AdicionarDependenteUseCaseScenario.ResultInvalidScenario;
import com.vanessaviagem.backoffice.application.ports.in.AdicionarDependenteUseCaseScenario.ResultValidScenario;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class AdicionarDependenteUseCaseTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.application.ports.in.AdicionarDependenteUseCaseTestDataProvider#commandValido")
    void shouldCreateCommandWithValidData(CommandValidScenario scenario) {
        AdicionarDependenteCommand command = new AdicionarDependenteCommand(
                scenario.titularId(),
                scenario.parentesco(),
                scenario.dadosPessoais()
        );

        assertThat(command.titularId()).isEqualTo(scenario.titularId());
        assertThat(command.parentesco()).isEqualTo(scenario.parentesco());
        assertThat(command.dadosPessoais()).isEqualTo(scenario.dadosPessoais());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.application.ports.in.AdicionarDependenteUseCaseTestDataProvider#commandInvalido")
    void shouldThrowWhenCommandHasInvalidData(CommandInvalidScenario scenario) {
        assertThatThrownBy(() -> new AdicionarDependenteCommand(
                scenario.titularId(),
                scenario.parentesco(),
                scenario.dadosPessoais()
        ))
                .isInstanceOf(scenario.expectedException())
                .hasMessage(scenario.expectedMessage());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.application.ports.in.AdicionarDependenteUseCaseTestDataProvider#resultValido")
    void shouldCreateResultWithValidData(ResultValidScenario scenario) {
        AdicionarDependenteResult result = new AdicionarDependenteResult(
                scenario.dependenteId(),
                scenario.dependente()
        );

        assertThat(result.dependenteId()).isEqualTo(scenario.dependenteId());
        assertThat(result.dependente()).isEqualTo(scenario.dependente());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.application.ports.in.AdicionarDependenteUseCaseTestDataProvider#resultInvalido")
    void shouldThrowWhenResultHasInvalidData(ResultInvalidScenario scenario) {
        assertThatThrownBy(() -> new AdicionarDependenteResult(
                scenario.dependenteId(),
                scenario.dependente()
        ))
                .isInstanceOf(scenario.expectedException())
                .hasMessage(scenario.expectedMessage());
    }
}
