package com.vanessaviagem.backoffice.domain.model.cliente;

import com.vanessaviagem.backoffice.domain.model.enums.TipoContato;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a customer contact.
 */
public record ClienteContato(
        UUID id,
        TipoContato tipo,
        String valor,
        boolean principal
) {

    public ClienteContato {
        Objects.requireNonNull(id, "id eh obrigatorio");
        Objects.requireNonNull(tipo, "tipo eh obrigatorio");
        Objects.requireNonNull(valor, "valor eh obrigatorio");
        if (valor.isBlank()) {
            throw new IllegalArgumentException("valor nao pode ser vazio");
        }
    }

    /**
     * Creates a new contact with generated ID.
     */
    public static ClienteContato criar(
            TipoContato tipo,
            String valor,
            boolean principal
    ) {
        return new ClienteContato(
                UUID.randomUUID(),
                tipo,
                valor,
                principal
        );
    }

    /**
     * Creates a copy with a new ID (for persistence).
     */
    public ClienteContato withId(UUID newId) {
        return new ClienteContato(newId, tipo, valor, principal);
    }
}
