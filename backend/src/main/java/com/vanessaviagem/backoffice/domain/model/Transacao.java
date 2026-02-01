package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Registro imutável de uma transação de milhas.
 * Transações são append-only - não podem ser editadas ou removidas.
 */
public record Transacao(
        UUID id,
        UUID contaProgramaId,
        TipoTransacao tipo,
        long milhas,
        BigDecimal valorBRL,
        String fonte,
        String observacao,
        LocalDateTime data,
        LocalDateTime criadoEm
) {
    public Transacao {
        Objects.requireNonNull(contaProgramaId, "contaProgramaId é obrigatório");
        Objects.requireNonNull(tipo, "tipo é obrigatório");
        if (milhas <= 0) {
            throw new IllegalArgumentException("milhas deve ser positivo");
        }
        if (valorBRL == null) {
            valorBRL = BigDecimal.ZERO;
        }
        if (data == null) {
            data = LocalDateTime.now();
        }
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }

    /**
     * Cria uma transação de COMPRA.
     */
    public static Transacao criarCompra(UUID contaProgramaId, long milhas, BigDecimal valor,
                                        String fonte, String observacao) {
        return new Transacao(
                UUID.randomUUID(),
                contaProgramaId,
                TipoTransacao.COMPRA,
                milhas,
                valor,
                fonte,
                observacao,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    /**
     * Cria uma transação de BONUS.
     */
    public static Transacao criarBonus(UUID contaProgramaId, long milhas,
                                       String fonte, String observacao) {
        return new Transacao(
                UUID.randomUUID(),
                contaProgramaId,
                TipoTransacao.BONUS,
                milhas,
                BigDecimal.ZERO,
                fonte,
                observacao,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    /**
     * Cria uma transação de VENDA.
     */
    public static Transacao criarVenda(UUID contaProgramaId, long milhas, BigDecimal valor,
                                       String observacao) {
        return new Transacao(
                UUID.randomUUID(),
                contaProgramaId,
                TipoTransacao.VENDA,
                milhas,
                valor,
                null,
                observacao,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
