package com.vanessaviagem.backoffice.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.vanessaviagem.backoffice.domain.model.MilhasScenario.ComIdFactoryInvalidaScenario;
import com.vanessaviagem.backoffice.domain.model.MilhasScenario.ComIdFactoryScenario;
import com.vanessaviagem.backoffice.domain.model.MilhasScenario.ComIdInstanceScenario;
import com.vanessaviagem.backoffice.domain.model.MilhasScenario.ComQuantidadeScenario;
import com.vanessaviagem.backoffice.domain.model.MilhasScenario.ComValorScenario;
import com.vanessaviagem.backoffice.domain.model.MilhasScenario.CriacaoInvalidaScenario;
import com.vanessaviagem.backoffice.domain.model.MilhasScenario.CriacaoValidaScenario;
import com.vanessaviagem.backoffice.domain.model.MilhasScenario.PrecoMedioScenario;
import com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas;
import java.math.BigDecimal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for {@link Milhas} domain entity.
 */
class MilhasPrecoMedioTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.MilhasTestDataProvider#cenariosPrecoMedio")
    void shouldCalculatePrecoPorMilheiro(PrecoMedioScenario scenario) {
        Milhas milhas = Milhas.criar(TipoProgramaMilhas.LATAM_PASS, scenario.quantidade(), scenario.valor());

        BigDecimal preco = milhas.precoPorMilheiro();

        assertThat(preco).isEqualByComparingTo(scenario.esperado());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.MilhasTestDataProvider#criacaoValida")
    void shouldCreateMilhasWithValidData(CriacaoValidaScenario scenario) {
        Milhas milhas = Milhas.criar(scenario.programa(), scenario.quantidade(), scenario.valor());

        assertThat(milhas.programa()).isEqualTo(scenario.programa());
        assertThat(milhas.quantidade()).isEqualTo(scenario.quantidade());
        assertThat(milhas.valor()).isEqualTo(scenario.valor());
        assertThat(milhas.id()).isNull();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.MilhasTestDataProvider#criacaoInvalida")
    void shouldThrowWhenCreatingMilhasWithInvalidData(CriacaoInvalidaScenario scenario) {
        assertThatThrownBy(() -> Milhas.criar(scenario.programa(), scenario.quantidade(), scenario.valor()))
                .isInstanceOf(scenario.expectedException())
                .hasMessage(scenario.expectedMessage());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.MilhasTestDataProvider#comIdFactory")
    void shouldCreateMilhasWithIdUsingComIdFactory(ComIdFactoryScenario scenario) {
        Milhas milhas = Milhas.comId(
                scenario.id(),
                scenario.programa(),
                scenario.quantidade(),
                scenario.valor()
        );

        assertThat(milhas.id()).isEqualTo(scenario.id());
        assertThat(milhas.programa()).isEqualTo(scenario.programa());
        assertThat(milhas.quantidade()).isEqualTo(scenario.quantidade());
        assertThat(milhas.valor()).isEqualTo(scenario.valor());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.MilhasTestDataProvider#comIdFactoryInvalida")
    void shouldThrowWhenCreatingMilhasWithInvalidDataUsingComIdFactory(ComIdFactoryInvalidaScenario scenario) {
        assertThatThrownBy(() -> Milhas.comId(
                scenario.id(),
                scenario.programa(),
                scenario.quantidade(),
                scenario.valor()
        ))
                .isInstanceOf(scenario.expectedException())
                .hasMessage(scenario.expectedMessage());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.MilhasTestDataProvider#comQuantidade")
    void shouldUpdateQuantidadeUsingComQuantidade(ComQuantidadeScenario scenario) {
        Milhas atualizada = scenario.milhasOriginal().comQuantidade(scenario.novaQuantidade());

        assertThat(atualizada.quantidade()).isEqualTo(scenario.novaQuantidade());
        assertThat(atualizada.id()).isEqualTo(scenario.milhasOriginal().id());
        assertThat(atualizada.programa()).isEqualTo(scenario.milhasOriginal().programa());
        assertThat(atualizada.valor()).isEqualTo(scenario.milhasOriginal().valor());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.MilhasTestDataProvider#comValor")
    void shouldUpdateValorUsingComValor(ComValorScenario scenario) {
        Milhas atualizada = scenario.milhasOriginal().comValor(scenario.novoValor());

        assertThat(atualizada.valor()).isEqualTo(scenario.novoValor());
        assertThat(atualizada.id()).isEqualTo(scenario.milhasOriginal().id());
        assertThat(atualizada.programa()).isEqualTo(scenario.milhasOriginal().programa());
        assertThat(atualizada.quantidade()).isEqualTo(scenario.milhasOriginal().quantidade());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.MilhasTestDataProvider#comIdInstance")
    void shouldUpdateIdUsingComIdInstanceMethod(ComIdInstanceScenario scenario) {
        Milhas atualizada = scenario.milhasOriginal().comId(scenario.novoId());

        assertThat(atualizada.id()).isEqualTo(scenario.novoId());
        assertThat(atualizada.programa()).isEqualTo(scenario.milhasOriginal().programa());
        assertThat(atualizada.quantidade()).isEqualTo(scenario.milhasOriginal().quantidade());
        assertThat(atualizada.valor()).isEqualTo(scenario.milhasOriginal().valor());
    }
}
