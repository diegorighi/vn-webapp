package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.Sexo;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record Pessoa(
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
        LocalDate updatedAt
) {

    public Pessoa {
        Objects.requireNonNull(pessoaId, "pessoaId eh obrigatorio");
        Objects.requireNonNull(primeiroNome, "primeiroNome eh obrigatorio");
        Objects.requireNonNull(sobrenome, "sobrenome eh obrigatorio");
        Objects.requireNonNull(dataNascimento, "dataNascimento eh obrigatorio");
        Objects.requireNonNull(sexo, "sexo eh obrigatorio");
        Objects.requireNonNull(documentos, "documentos eh obrigatorio");
        Objects.requireNonNull(enderecos, "enderecos eh obrigatorio");
        Objects.requireNonNull(contatos, "contatos eh obrigatorio");
    }
}
