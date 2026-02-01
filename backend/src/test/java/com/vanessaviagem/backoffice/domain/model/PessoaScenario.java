package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.Sexo;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PessoaScenario(
        String descricao,
        UUID pessoaId,
        String primeiroNome,
        String nomeDoMeio,
        String sobrenome,
        LocalDate dataNascimento,
        Sexo sexo,
        List<Documento> documentos,
        List<Endereco> enderecos,
        List<Contato> contatos,
        LocalDate createdAt,
        LocalDate updatedAt,
        Class<? extends Exception> expectedException,
        String expectedMessage
) {
    @Override
    public String toString() {
        return descricao;
    }
}
