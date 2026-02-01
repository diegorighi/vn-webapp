package com.vanessaviagem.backoffice.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ViagemTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.ViagemTestDataProvider#criacaoValida")
    void shouldCreateViagemWithValidData(ViagemScenario scenario) {
        Viagem viagem = new Viagem(
                scenario.viagemId(),
                scenario.localizador(),
                scenario.trecho(),
                scenario.data(),
                scenario.assento(),
                scenario.companhiaAereaList(),
                scenario.moeda(),
                scenario.precoTotal(),
                scenario.status(),
                scenario.createdAt(),
                scenario.updatedAt()
        );

        assertThat(viagem.viagemId()).isEqualTo(scenario.viagemId());
        assertThat(viagem.localizador()).isEqualTo(scenario.localizador());
        assertThat(viagem.trecho()).isEqualTo(scenario.trecho());
        assertThat(viagem.data()).isEqualTo(scenario.data());
        assertThat(viagem.assento()).isEqualTo(scenario.assento());
        assertThat(viagem.companhiaAereaList()).isEqualTo(scenario.companhiaAereaList());
        assertThat(viagem.moeda()).isEqualTo(scenario.moeda());
        assertThat(viagem.precoTotal()).isEqualTo(scenario.precoTotal());
        assertThat(viagem.status()).isEqualTo(scenario.status());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.ViagemTestDataProvider#validacaoNulos")
    void shouldThrowWhenRequiredFieldIsNull(ViagemScenario scenario) {
        assertThatThrownBy(() -> new Viagem(
                scenario.viagemId(),
                scenario.localizador(),
                scenario.trecho(),
                scenario.data(),
                scenario.assento(),
                scenario.companhiaAereaList(),
                scenario.moeda(),
                scenario.precoTotal(),
                scenario.status(),
                scenario.createdAt(),
                scenario.updatedAt()
        ))
                .isInstanceOf(scenario.expectedException())
                .hasMessage(scenario.expectedMessage());
    }
}
