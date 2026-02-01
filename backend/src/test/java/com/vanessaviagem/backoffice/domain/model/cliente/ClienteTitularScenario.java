package com.vanessaviagem.backoffice.domain.model.cliente;

import com.vanessaviagem.backoffice.domain.model.Pessoa;
import com.vanessaviagem.backoffice.domain.model.Viagem;
import java.util.List;
import java.util.UUID;

public record ClienteTitularScenario(
        String descricao,
        UUID clienteId,
        Pessoa dadosPessoais,
        List<Viagem> viagens,
        boolean ativo,
        List<ClienteDependente> dependentes,
        Class<? extends Exception> expectedException,
        String expectedMessage
) {
    @Override
    public String toString() {
        return descricao;
    }
}
