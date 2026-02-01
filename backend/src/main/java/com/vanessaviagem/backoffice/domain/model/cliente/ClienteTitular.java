package com.vanessaviagem.backoffice.domain.model.cliente;

import com.vanessaviagem.backoffice.domain.model.Pessoa;
import com.vanessaviagem.backoffice.domain.model.Viagem;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate representing a titular customer.
 * A titular can have dependents linked to them.
 * When a titular is deleted, all dependents are cascade deleted.
 */
public record ClienteTitular(
        UUID clienteId,
        Pessoa dadosPessoais,
        List<Viagem> viagens,
        boolean ativo,
        List<ClienteDependente> dependentes
) {

    public ClienteTitular {
        Objects.requireNonNull(clienteId, "clienteId eh obrigatorio");
        Objects.requireNonNull(dadosPessoais, "dadosPessoais eh obrigatorio");
        Objects.requireNonNull(viagens, "viagens eh obrigatorio");
        Objects.requireNonNull(dependentes, "dependentes eh obrigatorio");
    }

    /**
     * Creates a copy with updated active status.
     * If deactivating, this does NOT automatically deactivate dependents -
     * that should be handled by the service layer.
     */
    public ClienteTitular comStatus(boolean novoAtivo) {
        return new ClienteTitular(
                this.clienteId,
                this.dadosPessoais,
                this.viagens,
                novoAtivo,
                this.dependentes
        );
    }

    /**
     * Checks if this titular is active.
     */
    public boolean isAtivo() {
        return ativo;
    }

    /**
     * Returns the full name from dados pessoais.
     */
    public String nomeCompleto() {
        return dadosPessoais.primeiroNome() + " " + dadosPessoais.sobrenome();
    }

    /**
     * Returns the number of active dependents.
     */
    public int quantidadeDependentesAtivos() {
        return (int) dependentes.stream().filter(ClienteDependente::ativo).count();
    }
}
