package com.vanessaviagem.backoffice.application.ports.in;

import com.vanessaviagem.backoffice.domain.model.Milhas;
import com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Input port for registering new miles entries.
 * This use case handles the creation of miles entries for clients.
 */
public interface RegistrarMilhasUseCase {

    /**
     * Registers a new miles entry for a client.
     *
     * @param command the command containing the miles data
     * @return the result with the created miles entry
     * @throws IllegalArgumentException if command data is invalid
     * @throws com.vanessaviagem.backoffice.domain.exceptions.ClienteNaoEncontradoException
     *         if the client is not found
     */
    RegistrarMilhasResult execute(RegistrarMilhasCommand command);

    /**
     * Command object containing the data needed to register miles.
     *
     * @param clienteId the ID of the client who owns the miles
     * @param programa the loyalty program type
     * @param quantidade the quantity of miles
     * @param valor the monetary value in BRL
     */
    record RegistrarMilhasCommand(
            UUID clienteId,
            TipoProgramaMilhas programa,
            int quantidade,
            BigDecimal valor
    ) {
        public RegistrarMilhasCommand {
            Objects.requireNonNull(clienteId, "clienteId eh obrigatorio");
            Objects.requireNonNull(programa, "programa eh obrigatorio");
            Objects.requireNonNull(valor, "valor eh obrigatorio");
            if (quantidade <= 0) {
                throw new IllegalArgumentException("quantidade deve ser positiva");
            }
        }

        /**
         * Converts this command to a Milhas domain object.
         *
         * @return a new Milhas instance without ID
         */
        public Milhas toMilhas() {
            return Milhas.criar(programa, quantidade, valor);
        }
    }

    /**
     * Result object containing the outcome of miles registration.
     *
     * @param milhasId the generated miles entry ID
     * @param milhas the registered miles entry
     */
    record RegistrarMilhasResult(
            UUID milhasId,
            Milhas milhas
    ) {
        public RegistrarMilhasResult {
            Objects.requireNonNull(milhasId, "milhasId eh obrigatorio");
            Objects.requireNonNull(milhas, "milhas eh obrigatorio");
        }

        /**
         * Factory method to create a result from a Milhas domain object.
         *
         * @param milhas the registered miles entry
         * @return a new result instance
         */
        public static RegistrarMilhasResult from(Milhas milhas) {
            Objects.requireNonNull(milhas, "milhas eh obrigatorio");
            Objects.requireNonNull(milhas.id(), "milhas.id eh obrigatorio");
            return new RegistrarMilhasResult(milhas.id(), milhas);
        }
    }
}
