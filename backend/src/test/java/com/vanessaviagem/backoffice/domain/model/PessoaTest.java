package com.vanessaviagem.backoffice.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class PessoaTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.PessoaTestDataProvider#criacaoValida")
    void shouldCreatePessoaWithValidData(PessoaScenario scenario) {
        Pessoa pessoa = new Pessoa(
                scenario.pessoaId(),
                scenario.primeiroNome(),
                scenario.nomeDoMeio(),
                scenario.sobrenome(),
                scenario.dataNascimento(),
                scenario.sexo(),
                scenario.documentos(),
                scenario.enderecos(),
                scenario.contatos(),
                scenario.createdAt(),
                scenario.updatedAt()
        );

        assertThat(pessoa.pessoaId()).isEqualTo(scenario.pessoaId());
        assertThat(pessoa.primeiroNome()).isEqualTo(scenario.primeiroNome());
        assertThat(pessoa.nomeDoMeio()).isEqualTo(scenario.nomeDoMeio());
        assertThat(pessoa.sobrenome()).isEqualTo(scenario.sobrenome());
        assertThat(pessoa.dataNascimento()).isEqualTo(scenario.dataNascimento());
        assertThat(pessoa.sexo()).isEqualTo(scenario.sexo());
        assertThat(pessoa.documentos()).isEqualTo(scenario.documentos());
        assertThat(pessoa.enderecos()).isEqualTo(scenario.enderecos());
        assertThat(pessoa.contatos()).isEqualTo(scenario.contatos());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.PessoaTestDataProvider#validacaoNulos")
    void shouldThrowWhenRequiredFieldIsNull(PessoaScenario scenario) {
        assertThatThrownBy(() -> new Pessoa(
                scenario.pessoaId(),
                scenario.primeiroNome(),
                scenario.nomeDoMeio(),
                scenario.sobrenome(),
                scenario.dataNascimento(),
                scenario.sexo(),
                scenario.documentos(),
                scenario.enderecos(),
                scenario.contatos(),
                scenario.createdAt(),
                scenario.updatedAt()
        ))
                .isInstanceOf(scenario.expectedException())
                .hasMessage(scenario.expectedMessage());
    }
}
