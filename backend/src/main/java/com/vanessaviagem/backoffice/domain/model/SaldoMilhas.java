package com.vanessaviagem.backoffice.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Registro de saldo de milhas de um programa de fidelidade.
 * Entrada manual para consolidar saldos da familia.
 */
public record SaldoMilhas(
        UUID id,
        UUID tenantId,
        String programa,
        String owner,
        long quantidade,
        LocalDateTime updatedAt,
        LocalDateTime createdAt
) {
    public SaldoMilhas {
        Objects.requireNonNull(tenantId, "tenantId é obrigatório");
        Objects.requireNonNull(programa, "programa é obrigatório");
        Objects.requireNonNull(owner, "owner é obrigatório");
        if (quantidade < 0) {
            throw new IllegalArgumentException("quantidade não pode ser negativa");
        }
    }

    /**
     * Cria um novo registro de saldo.
     */
    public static SaldoMilhas criar(UUID tenantId, String programa, String owner, long quantidade) {
        LocalDateTime agora = LocalDateTime.now();
        return new SaldoMilhas(
                UUID.randomUUID(),
                tenantId,
                programa.trim(),
                owner.trim(),
                quantidade,
                agora,
                agora
        );
    }

    /**
     * Atualiza a quantidade de milhas.
     */
    public SaldoMilhas atualizarQuantidade(long novaQuantidade) {
        return new SaldoMilhas(
                this.id,
                this.tenantId,
                this.programa,
                this.owner,
                novaQuantidade,
                LocalDateTime.now(),
                this.createdAt
        );
    }
}
