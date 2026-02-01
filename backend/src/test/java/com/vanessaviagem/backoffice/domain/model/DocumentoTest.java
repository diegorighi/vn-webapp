package com.vanessaviagem.backoffice.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class DocumentoTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.DocumentoTestDataProvider#criacaoValida")
    void shouldCreateDocumentoWithValidData(DocumentoScenario scenario) {
        Documento documento = new Documento(
                scenario.tipo(),
                scenario.numero(),
                scenario.validade(),
                scenario.emitidoEm()
        );

        assertThat(documento.tipo()).isEqualTo(scenario.tipo());
        assertThat(documento.numero()).isEqualTo(scenario.numero());
        assertThat(documento.validade()).isEqualTo(scenario.validade());
        assertThat(documento.emitidoEm()).isEqualTo(scenario.emitidoEm());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.DocumentoTestDataProvider#validacaoNulos")
    void shouldThrowWhenRequiredFieldIsNull(DocumentoScenario scenario) {
        assertThatThrownBy(() -> new Documento(
                scenario.tipo(),
                scenario.numero(),
                scenario.validade(),
                scenario.emitidoEm()
        ))
                .isInstanceOf(scenario.expectedException())
                .hasMessage(scenario.expectedMessage());
    }
}
