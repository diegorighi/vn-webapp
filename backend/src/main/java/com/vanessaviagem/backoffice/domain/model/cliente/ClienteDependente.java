package com.vanessaviagem.backoffice.domain.model.cliente;

import com.vanessaviagem.backoffice.domain.model.Pessoa;
import com.vanessaviagem.backoffice.domain.model.Viagem;
import com.vanessaviagem.backoffice.domain.model.enums.Parentesco;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record ClienteDependente(
        UUID clienteId,
        UUID titularId,
        Parentesco parentesco,
        Pessoa dadosPessoais,
        List<Viagem> viagens,
        boolean ativo
) {

    public ClienteDependente {
        Objects.requireNonNull(clienteId, "clienteId eh obrigatorio");
        Objects.requireNonNull(titularId, "titularId eh obrigatorio");
        Objects.requireNonNull(parentesco, "parentesco eh obrigatorio");
        Objects.requireNonNull(dadosPessoais, "dadosPessoais eh obrigatorio");
        Objects.requireNonNull(viagens, "viagens eh obrigatorio");
    }

    /**
     * Creates a new ClienteDependente with the specified active status.
     *
     * @param ativo the new active status
     * @return a new ClienteDependente with updated status
     */
    public ClienteDependente comStatus(boolean ativo) {
        return new ClienteDependente(
                this.clienteId,
                this.titularId,
                this.parentesco,
                this.dadosPessoais,
                this.viagens,
                ativo
        );
    }
}
