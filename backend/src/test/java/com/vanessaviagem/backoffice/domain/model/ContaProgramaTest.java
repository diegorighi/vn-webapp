package com.vanessaviagem.backoffice.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.vanessaviagem.backoffice.domain.model.ContaProgramaScenario.AplicarBonusInvalidoScenario;
import com.vanessaviagem.backoffice.domain.model.ContaProgramaScenario.AplicarBonusScenario;
import com.vanessaviagem.backoffice.domain.model.ContaProgramaScenario.AplicarCompraInvalidoScenario;
import com.vanessaviagem.backoffice.domain.model.ContaProgramaScenario.AplicarCompraScenario;
import com.vanessaviagem.backoffice.domain.model.ContaProgramaScenario.AplicarVendaInvalidoScenario;
import com.vanessaviagem.backoffice.domain.model.ContaProgramaScenario.AplicarVendaScenario;
import com.vanessaviagem.backoffice.domain.model.ContaProgramaScenario.ConstrutorInvalidoScenario;
import com.vanessaviagem.backoffice.domain.model.ContaProgramaScenario.CriarInvalidoScenario;
import com.vanessaviagem.backoffice.domain.model.ContaProgramaScenario.CriarScenario;
import com.vanessaviagem.backoffice.domain.model.ContaProgramaScenario.FluxoCompletoScenario;
import com.vanessaviagem.backoffice.domain.model.ContaProgramaScenario.PodeSacarScenario;
import com.vanessaviagem.backoffice.domain.model.ContaProgramaScenario.TemSaldoScenario;
import java.math.BigDecimal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for {@link ContaPrograma} aggregate.
 */
class ContaProgramaTest {

    private static final String PROVIDER = "com.vanessaviagem.backoffice.domain.model.ContaProgramaTestDataProvider#";

    @ParameterizedTest(name = "{0}")
    @MethodSource(PROVIDER + "criarValido")
    void shouldCreateContaProgramaWithValidData(CriarScenario scenario) {
        ContaPrograma conta = ContaPrograma.criar(
                scenario.tenantId(),
                scenario.programaId(),
                scenario.programaNome(),
                scenario.owner()
        );

        assertThat(conta.id()).isNotNull();
        assertThat(conta.tenantId()).isEqualTo(scenario.tenantId());
        assertThat(conta.programaId()).isEqualTo(scenario.programaId());
        assertThat(conta.programaNome()).isEqualTo(scenario.programaNome().trim());
        assertThat(conta.owner()).isEqualTo(scenario.owner().trim());
        assertThat(conta.saldoMilhas()).isZero();
        assertThat(conta.custoBaseTotalBRL()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(conta.custoMedioMilheiroAtual()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(conta.criadoEm()).isNotNull();
        assertThat(conta.atualizadoEm()).isNotNull();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(PROVIDER + "criarInvalido")
    void shouldThrowWhenCreatingContaProgramaWithInvalidData(CriarInvalidoScenario scenario) {
        assertThatThrownBy(() -> ContaPrograma.criar(
                scenario.tenantId(),
                scenario.programaId(),
                scenario.programaNome(),
                scenario.owner()
        ))
                .isInstanceOf(scenario.expectedException())
                .hasMessageContaining(scenario.expectedMessage());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(PROVIDER + "construtorInvalido")
    void shouldThrowWhenConstructorReceivesInvalidData(ConstrutorInvalidoScenario scenario) {
        assertThatThrownBy(() -> new ContaPrograma(
                scenario.id(),
                scenario.tenantId(),
                scenario.programaId(),
                scenario.programaNome(),
                scenario.owner(),
                scenario.saldoMilhas(),
                scenario.custoBaseTotalBRL(),
                scenario.custoMedioMilheiroAtual(),
                scenario.criadoEm(),
                scenario.atualizadoEm()
        ))
                .isInstanceOf(scenario.expectedException())
                .hasMessageContaining(scenario.expectedMessage());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(PROVIDER + "aplicarCompra")
    void shouldApplyCompraCorrectly(AplicarCompraScenario scenario) {
        ContaPrograma resultado = scenario.contaInicial().aplicarCompra(
                scenario.milhasCompradas(),
                scenario.valorCompra()
        );

        assertThat(resultado.saldoMilhas()).isEqualTo(scenario.saldoEsperado());
        assertThat(resultado.custoBaseTotalBRL()).isEqualByComparingTo(scenario.custoBaseEsperado());
        assertThat(resultado.custoMedioMilheiroAtual()).isEqualByComparingTo(scenario.custoMedioEsperado());
        assertThat(resultado.id()).isEqualTo(scenario.contaInicial().id());
        assertThat(resultado.tenantId()).isEqualTo(scenario.contaInicial().tenantId());
        assertThat(resultado.programaId()).isEqualTo(scenario.contaInicial().programaId());
        assertThat(resultado.atualizadoEm()).isAfterOrEqualTo(scenario.contaInicial().atualizadoEm());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(PROVIDER + "aplicarCompraInvalido")
    void shouldThrowWhenApplyingInvalidCompra(AplicarCompraInvalidoScenario scenario) {
        assertThatThrownBy(() -> scenario.contaInicial().aplicarCompra(
                scenario.milhasCompradas(),
                scenario.valorCompra()
        ))
                .isInstanceOf(scenario.expectedException())
                .hasMessageContaining(scenario.expectedMessage());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(PROVIDER + "aplicarBonus")
    void shouldApplyBonusCorrectly(AplicarBonusScenario scenario) {
        ContaPrograma resultado = scenario.contaInicial().aplicarBonus(scenario.milhasBonus());

        assertThat(resultado.saldoMilhas()).isEqualTo(scenario.saldoEsperado());
        assertThat(resultado.custoBaseTotalBRL()).isEqualByComparingTo(scenario.custoBaseEsperado());
        assertThat(resultado.custoMedioMilheiroAtual()).isEqualByComparingTo(scenario.custoMedioEsperado());
        assertThat(resultado.id()).isEqualTo(scenario.contaInicial().id());
        assertThat(resultado.atualizadoEm()).isAfterOrEqualTo(scenario.contaInicial().atualizadoEm());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(PROVIDER + "aplicarBonusInvalido")
    void shouldThrowWhenApplyingInvalidBonus(AplicarBonusInvalidoScenario scenario) {
        assertThatThrownBy(() -> scenario.contaInicial().aplicarBonus(scenario.milhasBonus()))
                .isInstanceOf(scenario.expectedException())
                .hasMessageContaining(scenario.expectedMessage());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(PROVIDER + "aplicarVenda")
    void shouldApplyVendaCorrectly(AplicarVendaScenario scenario) {
        ResultadoVenda resultado = scenario.contaInicial().aplicarVenda(
                scenario.milhasVendidas(),
                scenario.valorVenda()
        );

        ContaPrograma contaAtualizada = resultado.contaAtualizada();
        assertThat(contaAtualizada.saldoMilhas()).isEqualTo(scenario.saldoEsperado());
        assertThat(contaAtualizada.custoBaseTotalBRL()).isEqualByComparingTo(scenario.custoBaseEsperado());
        assertThat(contaAtualizada.custoMedioMilheiroAtual()).isEqualByComparingTo(scenario.custoMedioEsperado());
        assertThat(resultado.custoRemovido()).isEqualByComparingTo(scenario.custoRemovidoEsperado());
        assertThat(resultado.lucro()).isEqualByComparingTo(scenario.lucroEsperado());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(PROVIDER + "aplicarVendaInvalido")
    void shouldThrowWhenApplyingInvalidVenda(AplicarVendaInvalidoScenario scenario) {
        assertThatThrownBy(() -> scenario.contaInicial().aplicarVenda(
                scenario.milhasVendidas(),
                scenario.valorVenda()
        ))
                .isInstanceOf(scenario.expectedException())
                .hasMessageContaining(scenario.expectedMessage());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(PROVIDER + "fluxoCompleto")
    void shouldHandleCompleteFlowCorrectly(FluxoCompletoScenario scenario) {
        // Create account
        ContaPrograma conta = ContaPrograma.criar(
                scenario.tenantId(),
                scenario.programaId(),
                scenario.programaNome(),
                scenario.owner()
        );

        // Apply purchase
        conta = conta.aplicarCompra(scenario.milhasCompra1(), scenario.valorCompra1());

        // Apply bonus if any
        if (scenario.milhasBonus() > 0) {
            conta = conta.aplicarBonus(scenario.milhasBonus());
        }

        // Apply sale
        ResultadoVenda resultado = conta.aplicarVenda(scenario.milhasVenda(), scenario.valorVenda());

        assertThat(resultado.contaAtualizada().saldoMilhas()).isEqualTo(scenario.saldoFinalEsperado());
        assertThat(resultado.lucro()).isEqualByComparingTo(scenario.lucroEsperado());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(PROVIDER + "temSaldo")
    void shouldCheckTemSaldoCorrectly(TemSaldoScenario scenario) {
        assertThat(scenario.conta().temSaldo()).isEqualTo(scenario.esperado());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(PROVIDER + "podeSacar")
    void shouldCheckPodeSacarCorrectly(PodeSacarScenario scenario) {
        assertThat(scenario.conta().podeSacar(scenario.milhas())).isEqualTo(scenario.esperado());
    }
}
