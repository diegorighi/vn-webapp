package com.vanessaviagem.backoffice.domain.model.cliente;

import com.vanessaviagem.backoffice.domain.model.enums.Parentesco;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents the relationship between a dependent customer and their titular.
 * This is a linking entity that stores the parentesco (family relationship).
 */
public record ClienteDependenteVinculo(
        UUID clienteId,
        UUID titularId,
        Parentesco parentesco
) {

    public ClienteDependenteVinculo {
        Objects.requireNonNull(clienteId, "clienteId eh obrigatorio");
        Objects.requireNonNull(titularId, "titularId eh obrigatorio");
        Objects.requireNonNull(parentesco, "parentesco eh obrigatorio");
        if (clienteId.equals(titularId)) {
            throw new IllegalArgumentException("dependente nao pode ser seu proprio titular");
        }
    }

    /**
     * Creates a new vinculo.
     */
    public static ClienteDependenteVinculo criar(UUID clienteId, UUID titularId, Parentesco parentesco) {
        return new ClienteDependenteVinculo(clienteId, titularId, parentesco);
    }
}
