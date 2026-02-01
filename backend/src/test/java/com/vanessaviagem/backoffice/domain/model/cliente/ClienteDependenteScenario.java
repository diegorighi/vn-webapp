package com.vanessaviagem.backoffice.domain.model.cliente;

import com.vanessaviagem.backoffice.domain.model.Pessoa;
import com.vanessaviagem.backoffice.domain.model.Viagem;
import com.vanessaviagem.backoffice.domain.model.enums.Parentesco;
import java.util.List;
import java.util.UUID;

public record ClienteDependenteScenario(
        String descricao,
        UUID clienteId,
        UUID titularId,
        Parentesco parentesco,
        Pessoa dadosPessoais,
        List<Viagem> viagens,
        boolean ativo,
        Class<? extends Exception> expectedException,
        String expectedMessage
) {
    @Override
    public String toString() {
        return descricao;
    }
}
