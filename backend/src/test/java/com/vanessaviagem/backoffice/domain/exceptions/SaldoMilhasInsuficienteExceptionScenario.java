package com.vanessaviagem.backoffice.domain.exceptions;

import java.util.UUID;

public record SaldoMilhasInsuficienteExceptionScenario(
        String descricao,
        UUID programaId,
        long saldoAtual,
        long milhasSolicitadas,
        long expectedDeficit
) {
    @Override
    public String toString() {
        return descricao;
    }
}
