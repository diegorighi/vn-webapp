package com.vanessaviagem.backoffice.domain.model;

import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value object representing rounding configuration for monetary calculations.
 * Defines how decimal values should be rounded for a loyalty program.
 *
 * <p>This is immutable by design (Java record) and encapsulates
 * the rounding rules for cost calculations.</p>
 *
 * @param casasDecimais the number of decimal places to use (must be between 0 and 6)
 * @param modoArredondamento the rounding mode to apply
 */
public record ConfigArredondamento(
        int casasDecimais,
        RoundingMode modoArredondamento
) {

    /**
     * Default configuration with 4 decimal places and HALF_UP rounding.
     */
    public static final ConfigArredondamento DEFAULT = new ConfigArredondamento(4, RoundingMode.HALF_UP);

    /**
     * Compact constructor with validation.
     */
    public ConfigArredondamento {
        Objects.requireNonNull(modoArredondamento, "modoArredondamento eh obrigatorio");
        if (casasDecimais < 0 || casasDecimais > 6) {
            throw new IllegalArgumentException("casasDecimais deve estar entre 0 e 6");
        }
    }

    /**
     * Creates a configuration with the specified decimal places and default HALF_UP rounding.
     *
     * @param casasDecimais the number of decimal places
     * @return a new ConfigArredondamento instance
     */
    public static ConfigArredondamento comCasasDecimais(int casasDecimais) {
        return new ConfigArredondamento(casasDecimais, RoundingMode.HALF_UP);
    }
}
