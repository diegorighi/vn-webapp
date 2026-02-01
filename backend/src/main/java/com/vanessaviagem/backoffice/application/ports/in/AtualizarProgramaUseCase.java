package com.vanessaviagem.backoffice.application.ports.in;

import com.vanessaviagem.backoffice.domain.model.ConfigArredondamento;
import com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhas;
import com.vanessaviagem.backoffice.domain.model.enums.StatusPrograma;
import java.util.Objects;
import java.util.UUID;

/**
 * Input port for updating an existing loyalty program.
 * This use case handles modifications to program properties.
 */
public interface AtualizarProgramaUseCase {

    /**
     * Updates an existing loyalty program with the provided data.
     *
     * @param command the command containing the update data
     * @return the result containing the updated program
     * @throws com.vanessaviagem.backoffice.domain.exceptions.ProgramaNaoEncontradoException if program does not exist
     * @throws IllegalArgumentException if command data is invalid
     */
    AtualizarProgramaResult execute(AtualizarProgramaCommand command);

    /**
     * Command object containing the data needed to update a program.
     * Only non-null fields will be updated.
     *
     * @param programaId the ID of the program to update
     * @param brand the new brand name (optional, null means no change)
     * @param moeda the new currency code (optional, null means no change)
     * @param status the new status (optional, null means no change)
     * @param regrasArredondamento the new rounding configuration (optional, null means no change)
     */
    record AtualizarProgramaCommand(
            UUID programaId,
            String brand,
            String moeda,
            StatusPrograma status,
            ConfigArredondamento regrasArredondamento
    ) {
        /**
         * Compact constructor with validation.
         */
        public AtualizarProgramaCommand {
            Objects.requireNonNull(programaId, "programaId eh obrigatorio");
        }

        /**
         * Creates a command to update only the brand.
         *
         * @param programaId the program ID
         * @param brand the new brand name
         * @return a new command
         */
        public static AtualizarProgramaCommand comBrand(UUID programaId, String brand) {
            return new AtualizarProgramaCommand(programaId, brand, null, null, null);
        }

        /**
         * Creates a command to update only the status.
         *
         * @param programaId the program ID
         * @param status the new status
         * @return a new command
         */
        public static AtualizarProgramaCommand comStatus(UUID programaId, StatusPrograma status) {
            return new AtualizarProgramaCommand(programaId, null, null, status, null);
        }
    }

    /**
     * Result object containing the outcome of the update.
     *
     * @param programa the updated program
     */
    record AtualizarProgramaResult(
            ProgramaDeMilhas programa
    ) {
        /**
         * Compact constructor with validation.
         */
        public AtualizarProgramaResult {
            Objects.requireNonNull(programa, "programa eh obrigatorio");
        }
    }
}
