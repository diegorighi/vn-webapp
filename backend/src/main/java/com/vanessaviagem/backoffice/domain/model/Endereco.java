package com.vanessaviagem.backoffice.domain.model;

import java.util.Objects;

public record Endereco(
        boolean principal,
        String logradouro,
        Integer numero,
        String bairro,
        String cep,
        String cidade,
        String estado,
        String pais
) {

    public Endereco {
        Objects.requireNonNull(logradouro, "logradouro eh obrigatorio");
        Objects.requireNonNull(bairro, "bairro eh obrigatorio");
        Objects.requireNonNull(cep, "cep eh obrigatorio");
        Objects.requireNonNull(cidade, "cidade eh obrigatorio");
        Objects.requireNonNull(estado, "estado eh obrigatorio");
        Objects.requireNonNull(pais, "pais eh obrigatorio");
    }
}
