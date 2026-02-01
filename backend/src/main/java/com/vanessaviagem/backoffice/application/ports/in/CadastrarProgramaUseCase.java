package com.vanessaviagem.backoffice.application.ports.in;

import com.vanessaviagem.backoffice.domain.model.ConfigArredondamento;
import com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhas;
import java.util.Objects;
import java.util.UUID;

/**
 * Input port for registering a new loyalty program.
 * This use case handles the creation of new programs in the system.
 */
public interface CadastrarProgramaUseCase {

    /**
     * Registers a new loyalty program with the provided data.
     *
     * @param command the command containing the program data to register
     * @return the result containing the registered program
     * @throws IllegalArgumentException if command data is invalid
     * @throws com.vanessaviagem.backoffice.domain.exceptions.DomainException if business rules are violated
     */
    CadastrarProgramaResult execute(CadastrarProgramaCommand command);

    /**
     * Command object containing the data needed to register a new program.
     *
     * @param brand the unique brand name for the program
     * @param moeda the currency code (default: BRL)
     * @param regrasArredondamento the rounding configuration (optional, uses default if null)
     */
    record CadastrarProgramaCommand(
            String brand,
            String moeda,
            ConfigArredondamento regrasArredondamento
    ) {
        /**
         * Compact constructor with validation.
         */
        public CadastrarProgramaCommand {
            Objects.requireNonNull(brand, "brand eh obrigatorio");
            if (brand.isBlank()) {
                throw new IllegalArgumentException("brand nao pode estar vazio");
            }
            if (moeda == null) {
                moeda = ProgramaDeMilhas.MOEDA_PADRAO;
            }
            if (regrasArredondamento == null) {
                regrasArredondamento = ConfigArredondamento.DEFAULT;
            }
        }

        /**
         * Creates a command with just the brand, using defaults for other fields.
         *
         * @param brand the brand name
         * @return a new command with default values
         */
        public static CadastrarProgramaCommand comBrand(String brand) {
            return new CadastrarProgramaCommand(brand, null, null);
        }

        /**
         * Creates a command with brand and currency.
         *
         * @param brand the brand name
         * @param moeda the currency code
         * @return a new command with default rounding
         */
        public static CadastrarProgramaCommand comBrandEMoeda(String brand, String moeda) {
            return new CadastrarProgramaCommand(brand, moeda, null);
        }
    }

    /**
     * Result object containing the outcome of the registration.
     *
     * @param programaId the generated program ID
     * @param programa the registered program
     */
    record CadastrarProgramaResult(
            UUID programaId,
            ProgramaDeMilhas programa
    ) {
        /**
         * Compact constructor with validation.
         */
        public CadastrarProgramaResult {
            Objects.requireNonNull(programaId, "programaId eh obrigatorio");
            Objects.requireNonNull(programa, "programa eh obrigatorio");
        }
    }
}
