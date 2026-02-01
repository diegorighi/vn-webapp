package com.vanessaviagem.backoffice.domain.model.cliente;

import com.vanessaviagem.backoffice.domain.model.enums.TipoEndereco;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a customer address.
 */
public record ClienteEndereco(
        UUID id,
        TipoEndereco tipo,
        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        boolean principal
) {

    public ClienteEndereco {
        Objects.requireNonNull(id, "id eh obrigatorio");
        Objects.requireNonNull(tipo, "tipo eh obrigatorio");
        Objects.requireNonNull(cep, "cep eh obrigatorio");
        Objects.requireNonNull(logradouro, "logradouro eh obrigatorio");
        Objects.requireNonNull(numero, "numero eh obrigatorio");
        Objects.requireNonNull(bairro, "bairro eh obrigatorio");
        Objects.requireNonNull(cidade, "cidade eh obrigatorio");
        Objects.requireNonNull(estado, "estado eh obrigatorio");
        if (cep.isBlank()) {
            throw new IllegalArgumentException("cep nao pode ser vazio");
        }
        if (logradouro.isBlank()) {
            throw new IllegalArgumentException("logradouro nao pode ser vazio");
        }
    }

    /**
     * Creates a new address with generated ID.
     */
    public static ClienteEndereco criar(
            TipoEndereco tipo,
            String cep,
            String logradouro,
            String numero,
            String complemento,
            String bairro,
            String cidade,
            String estado,
            boolean principal
    ) {
        return new ClienteEndereco(
                UUID.randomUUID(),
                tipo,
                cep,
                logradouro,
                numero,
                complemento,
                bairro,
                cidade,
                estado,
                principal
        );
    }

    /**
     * Creates a copy with a new ID (for persistence).
     */
    public ClienteEndereco withId(UUID newId) {
        return new ClienteEndereco(newId, tipo, cep, logradouro, numero, complemento, bairro, cidade, estado, principal);
    }
}
