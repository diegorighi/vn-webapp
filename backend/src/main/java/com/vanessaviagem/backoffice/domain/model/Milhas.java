package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a miles balance entry for a specific loyalty program.
 * This value object captures the quantity of miles, their monetary value,
 * and allows calculation of the price per thousand miles (milheiro).
 *
 * <p>Invariants:
 * <ul>
 *   <li>programa cannot be null</li>
 *   <li>quantidade must be positive</li>
 *   <li>valor cannot be null</li>
 * </ul>
 */
public record Milhas(
        UUID id,
        TipoProgramaMilhas programa,
        int quantidade,
        BigDecimal valor
) {

    private static final int MILHEIRO = 1000;
    private static final int MILHEIRO_SCALE = 6;
    private static final int PRECO_SCALE = 4;

    /**
     * Compact constructor with validation.
     */
    public Milhas {
        Objects.requireNonNull(programa, "programa eh obrigatorio");
        Objects.requireNonNull(valor, "valor eh obrigatorio");
        if (quantidade <= 0) {
            throw new IllegalArgumentException("quantidade deve ser positiva");
        }
    }

    /**
     * Factory method to create Milhas without an ID (for new entries).
     *
     * @param programa the loyalty program type
     * @param quantidade the quantity of miles
     * @param valor the monetary value in BRL
     * @return a new Milhas instance with null ID
     */
    public static Milhas criar(TipoProgramaMilhas programa, int quantidade, BigDecimal valor) {
        return new Milhas(null, programa, quantidade, valor);
    }

    /**
     * Factory method to create Milhas with a specific ID.
     *
     * @param id the unique identifier
     * @param programa the loyalty program type
     * @param quantidade the quantity of miles
     * @param valor the monetary value in BRL
     * @return a new Milhas instance with the given ID
     */
    public static Milhas comId(UUID id, TipoProgramaMilhas programa, int quantidade, BigDecimal valor) {
        Objects.requireNonNull(id, "id eh obrigatorio");
        return new Milhas(id, programa, quantidade, valor);
    }

    /**
     * Calculates the price per thousand miles (milheiro).
     *
     * @return the price per 1000 miles with 4 decimal places precision
     */
    public BigDecimal precoPorMilheiro() {
        BigDecimal milheiros = BigDecimal.valueOf(quantidade)
                .divide(BigDecimal.valueOf(MILHEIRO), MILHEIRO_SCALE, RoundingMode.HALF_UP);
        return valor.divide(milheiros, PRECO_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Creates a new Milhas instance with updated quantity.
     *
     * @param novaQuantidade the new quantity
     * @return a new Milhas instance with the updated quantity
     */
    public Milhas comQuantidade(int novaQuantidade) {
        return new Milhas(this.id, this.programa, novaQuantidade, this.valor);
    }

    /**
     * Creates a new Milhas instance with updated value.
     *
     * @param novoValor the new monetary value
     * @return a new Milhas instance with the updated value
     */
    public Milhas comValor(BigDecimal novoValor) {
        return new Milhas(this.id, this.programa, this.quantidade, novoValor);
    }

    /**
     * Creates a new Milhas instance with the specified ID.
     *
     * @param novoId the ID to assign
     * @return a new Milhas instance with the given ID
     */
    public Milhas comId(UUID novoId) {
        Objects.requireNonNull(novoId, "id eh obrigatorio");
        return new Milhas(novoId, this.programa, this.quantidade, this.valor);
    }
}
