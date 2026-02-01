package com.vanessaviagem.backoffice.domain.model.cliente;

import com.vanessaviagem.backoffice.domain.model.enums.Parentesco;

import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate representing a dependent customer with their titular link.
 * Wraps a Cliente base (tipo=DEPENDENTE) with the vinculo information.
 */
public record ClienteDependenteCompleto(
        Cliente cliente,
        ClienteDependenteVinculo vinculo
) {

    public ClienteDependenteCompleto {
        Objects.requireNonNull(cliente, "cliente eh obrigatorio");
        Objects.requireNonNull(vinculo, "vinculo eh obrigatorio");
        if (!cliente.isDependente()) {
            throw new IllegalArgumentException("cliente deve ser do tipo DEPENDENTE");
        }
        if (!cliente.id().equals(vinculo.clienteId())) {
            throw new IllegalArgumentException("vinculo.clienteId deve corresponder ao cliente.id");
        }
    }

    /**
     * Creates a dependent with vinculo.
     */
    public static ClienteDependenteCompleto criar(Cliente cliente, UUID titularId, Parentesco parentesco) {
        ClienteDependenteVinculo vinculo = ClienteDependenteVinculo.criar(cliente.id(), titularId, parentesco);
        return new ClienteDependenteCompleto(cliente, vinculo);
    }

    /**
     * Returns the dependent's ID.
     */
    public UUID id() {
        return cliente.id();
    }

    /**
     * Returns the titular's ID this dependent is linked to.
     */
    public UUID titularId() {
        return vinculo.titularId();
    }

    /**
     * Returns the family relationship.
     */
    public Parentesco parentesco() {
        return vinculo.parentesco();
    }

    /**
     * Returns the dependent's full name.
     */
    public String nomeCompleto() {
        return cliente.nomeCompleto();
    }

    /**
     * Checks if the dependent is active.
     */
    public boolean isAtivo() {
        return cliente.ativo();
    }

    /**
     * Creates a copy with updated active status.
     */
    public ClienteDependenteCompleto comStatus(boolean novoAtivo) {
        return new ClienteDependenteCompleto(cliente.comAtivo(novoAtivo), vinculo);
    }
}
