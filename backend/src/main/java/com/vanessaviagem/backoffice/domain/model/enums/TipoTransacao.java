package com.vanessaviagem.backoffice.domain.model.enums;

/**
 * Represents the type of a miles transaction.
 * Each type has different rules for how it affects the balance and cost calculations.
 */
public enum TipoTransacao {
    /**
     * Purchase of miles. Increases balance and cost base.
     * valorBRL must be greater than zero.
     */
    COMPRA,

    /**
     * Sale of miles. Decreases balance and cost base proportionally.
     * valorBRL represents the sale price received.
     */
    VENDA,

    /**
     * Bonus miles (cashback, promotions, etc.). Increases balance without cost.
     * valorBRL should be zero as no monetary cost is incurred.
     */
    BONUS
}
