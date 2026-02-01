package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.StatusPrograma;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing a loyalty miles program.
 * Each program has a unique brand identifier and can be active or inactive.
 *
 * <p>This is a domain entity that encapsulates business rules for
 * loyalty programs. The brand must be unique across all programs.</p>
 *
 * @param id unique identifier for the program
 * @param brand unique brand name (e.g., "Smiles", "LATAM Pass")
 * @param status current status of the program (ATIVO or INATIVO)
 * @param moeda currency code for transactions (default: BRL)
 * @param regrasArredondamento rounding configuration for calculations
 */
public record ProgramaDeMilhas(
        UUID id,
        String brand,
        StatusPrograma status,
        String moeda,
        ConfigArredondamento regrasArredondamento
) {

    /**
     * Default currency for programs.
     */
    public static final String MOEDA_PADRAO = "BRL";

    /**
     * Compact constructor with validation of all invariants.
     */
    public ProgramaDeMilhas {
        Objects.requireNonNull(id, "id eh obrigatorio");
        Objects.requireNonNull(brand, "brand eh obrigatorio");
        Objects.requireNonNull(status, "status eh obrigatorio");
        Objects.requireNonNull(moeda, "moeda eh obrigatorio");
        Objects.requireNonNull(regrasArredondamento, "regrasArredondamento eh obrigatorio");

        if (brand.isBlank()) {
            throw new IllegalArgumentException("brand nao pode estar vazio");
        }
        if (moeda.isBlank()) {
            throw new IllegalArgumentException("moeda nao pode estar vazia");
        }
        if (moeda.length() != 3) {
            throw new IllegalArgumentException("moeda deve ter exatamente 3 caracteres (codigo ISO)");
        }
    }

    /**
     * Creates a new program with default values for optional fields.
     *
     * @param id unique identifier
     * @param brand brand name
     * @return a new active program with BRL currency and default rounding
     */
    public static ProgramaDeMilhas criar(UUID id, String brand) {
        return new ProgramaDeMilhas(
                id,
                brand,
                StatusPrograma.ATIVO,
                MOEDA_PADRAO,
                ConfigArredondamento.DEFAULT
        );
    }

    /**
     * Creates a new program with the specified currency.
     *
     * @param id unique identifier
     * @param brand brand name
     * @param moeda currency code
     * @return a new active program with default rounding
     */
    public static ProgramaDeMilhas criarComMoeda(UUID id, String brand, String moeda) {
        return new ProgramaDeMilhas(
                id,
                brand,
                StatusPrograma.ATIVO,
                moeda,
                ConfigArredondamento.DEFAULT
        );
    }

    /**
     * Checks if the program is currently active.
     *
     * @return true if the program status is ATIVO
     */
    public boolean isAtivo() {
        return status == StatusPrograma.ATIVO;
    }

    /**
     * Creates a copy of this program with INATIVO status.
     *
     * @return a new ProgramaDeMilhas instance with INATIVO status
     */
    public ProgramaDeMilhas desativar() {
        return new ProgramaDeMilhas(id, brand, StatusPrograma.INATIVO, moeda, regrasArredondamento);
    }

    /**
     * Creates a copy of this program with ATIVO status.
     *
     * @return a new ProgramaDeMilhas instance with ATIVO status
     */
    public ProgramaDeMilhas ativar() {
        return new ProgramaDeMilhas(id, brand, StatusPrograma.ATIVO, moeda, regrasArredondamento);
    }

    /**
     * Creates a copy of this program with updated rounding configuration.
     *
     * @param novaConfig the new rounding configuration
     * @return a new ProgramaDeMilhas instance with the updated configuration
     */
    public ProgramaDeMilhas comRegrasArredondamento(ConfigArredondamento novaConfig) {
        return new ProgramaDeMilhas(id, brand, status, moeda, novaConfig);
    }
}
