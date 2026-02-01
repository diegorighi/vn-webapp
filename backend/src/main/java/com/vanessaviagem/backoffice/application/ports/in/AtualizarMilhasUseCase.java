package com.vanessaviagem.backoffice.application.ports.in;

import com.vanessaviagem.backoffice.domain.model.Milhas;
import com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Input port for updating existing miles entries.
 * This use case handles modifications to miles entries.
 */
public interface AtualizarMilhasUseCase {

    /**
     * Updates an existing miles entry.
     *
     * @param command the command containing the updated miles data
     * @return the result with the updated miles entry
     * @throws IllegalArgumentException if command data is invalid
     * @throws com.vanessaviagem.backoffice.domain.exceptions.MilhasNaoEncontradaException
     *         if the miles entry is not found
     */
    AtualizarMilhasResult execute(AtualizarMilhasCommand command);

    /**
     * Command object containing the data needed to update miles.
     *
     * @param milhasId the ID of the miles entry to update
     * @param programa the loyalty program type (optional, null to keep current)
     * @param quantidade the new quantity of miles (optional, null to keep current)
     * @param valor the new monetary value in BRL (optional, null to keep current)
     */
    record AtualizarMilhasCommand(
            UUID milhasId,
            TipoProgramaMilhas programa,
            Integer quantidade,
            BigDecimal valor
    ) {
        public AtualizarMilhasCommand {
            Objects.requireNonNull(milhasId, "milhasId eh obrigatorio");
            if (quantidade != null && quantidade <= 0) {
                throw new IllegalArgumentException("quantidade deve ser positiva");
            }
        }

        /**
         * Applies the updates from this command to an existing Milhas.
         *
         * @param existing the existing miles entry to update
         * @return a new Milhas instance with the updates applied
         */
        public Milhas applyTo(Milhas existing) {
            Objects.requireNonNull(existing, "existing eh obrigatorio");

            TipoProgramaMilhas novoPrograma = programa != null ? programa : existing.programa();
            int novaQuantidade = quantidade != null ? quantidade : existing.quantidade();
            BigDecimal novoValor = valor != null ? valor : existing.valor();

            return Milhas.comId(existing.id(), novoPrograma, novaQuantidade, novoValor);
        }
    }

    /**
     * Result object containing the outcome of miles update.
     *
     * @param milhasId the ID of the updated miles entry
     * @param milhas the updated miles entry
     */
    record AtualizarMilhasResult(
            UUID milhasId,
            Milhas milhas
    ) {
        public AtualizarMilhasResult {
            Objects.requireNonNull(milhasId, "milhasId eh obrigatorio");
            Objects.requireNonNull(milhas, "milhas eh obrigatorio");
        }

        /**
         * Factory method to create a result from a Milhas domain object.
         *
         * @param milhas the updated miles entry
         * @return a new result instance
         */
        public static AtualizarMilhasResult from(Milhas milhas) {
            Objects.requireNonNull(milhas, "milhas eh obrigatorio");
            Objects.requireNonNull(milhas.id(), "milhas.id eh obrigatorio");
            return new AtualizarMilhasResult(milhas.id(), milhas);
        }
    }
}
