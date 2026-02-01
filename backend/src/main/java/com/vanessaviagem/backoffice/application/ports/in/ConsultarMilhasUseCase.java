package com.vanessaviagem.backoffice.application.ports.in;

import com.vanessaviagem.backoffice.domain.model.Milhas;
import com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Input port for querying miles entries.
 * This use case handles read operations for miles data.
 */
public interface ConsultarMilhasUseCase {

    /**
     * Finds a miles entry by its unique identifier.
     *
     * @param query the query containing the miles ID
     * @return an Optional containing the result if found, empty otherwise
     */
    Optional<ConsultarMilhasResult> buscarPorId(ConsultarMilhasPorIdQuery query);

    /**
     * Finds all miles entries for a specific client.
     *
     * @param query the query containing the client ID
     * @return the result with all miles entries for the client
     */
    ConsultarMilhasPorClienteResult buscarPorCliente(ConsultarMilhasPorClienteQuery query);

    /**
     * Finds all miles entries for a specific loyalty program.
     *
     * @param query the query containing the program type
     * @return the result with all miles entries for the program
     */
    ConsultarMilhasPorProgramaResult buscarPorPrograma(ConsultarMilhasPorProgramaQuery query);

    /**
     * Calculates the total miles balance for a client across all programs.
     *
     * @param query the query containing the client ID
     * @return the result with total miles count
     */
    SaldoTotalResult calcularSaldoTotal(ConsultarMilhasPorClienteQuery query);

    /**
     * Calculates the weighted average cost per thousand miles for a client.
     *
     * @param query the query containing the client ID
     * @return the result with the average cost per milheiro
     */
    CustoMedioMilheiroResult calcularCustoMedioMilheiro(ConsultarMilhasPorClienteQuery query);

    // --- Query Objects ---

    /**
     * Query object for finding miles by ID.
     *
     * @param milhasId the ID of the miles entry to find
     */
    record ConsultarMilhasPorIdQuery(UUID milhasId) {
        public ConsultarMilhasPorIdQuery {
            Objects.requireNonNull(milhasId, "milhasId eh obrigatorio");
        }
    }

    /**
     * Query object for finding miles by client.
     *
     * @param clienteId the ID of the client
     */
    record ConsultarMilhasPorClienteQuery(UUID clienteId) {
        public ConsultarMilhasPorClienteQuery {
            Objects.requireNonNull(clienteId, "clienteId eh obrigatorio");
        }
    }

    /**
     * Query object for finding miles by program.
     *
     * @param programa the loyalty program type
     */
    record ConsultarMilhasPorProgramaQuery(TipoProgramaMilhas programa) {
        public ConsultarMilhasPorProgramaQuery {
            Objects.requireNonNull(programa, "programa eh obrigatorio");
        }
    }

    // --- Result Objects ---

    /**
     * Result object containing a single miles entry.
     *
     * @param milhasId the miles entry ID
     * @param milhas the miles entry
     */
    record ConsultarMilhasResult(
            UUID milhasId,
            Milhas milhas
    ) {
        public ConsultarMilhasResult {
            Objects.requireNonNull(milhasId, "milhasId eh obrigatorio");
            Objects.requireNonNull(milhas, "milhas eh obrigatorio");
        }

        /**
         * Factory method to create a result from a Milhas domain object.
         *
         * @param milhas the miles entry
         * @return a new result instance
         */
        public static ConsultarMilhasResult from(Milhas milhas) {
            Objects.requireNonNull(milhas, "milhas eh obrigatorio");
            Objects.requireNonNull(milhas.id(), "milhas.id eh obrigatorio");
            return new ConsultarMilhasResult(milhas.id(), milhas);
        }
    }

    /**
     * Result object containing miles entries for a client.
     *
     * @param clienteId the client ID
     * @param milhasList the list of miles entries
     * @param totalEntries the total number of entries
     */
    record ConsultarMilhasPorClienteResult(
            UUID clienteId,
            List<Milhas> milhasList,
            int totalEntries
    ) {
        public ConsultarMilhasPorClienteResult {
            Objects.requireNonNull(clienteId, "clienteId eh obrigatorio");
            Objects.requireNonNull(milhasList, "milhasList eh obrigatorio");
            milhasList = List.copyOf(milhasList); // defensive copy
        }
    }

    /**
     * Result object containing miles entries for a program.
     *
     * @param programa the program type
     * @param milhasList the list of miles entries
     * @param totalEntries the total number of entries
     */
    record ConsultarMilhasPorProgramaResult(
            TipoProgramaMilhas programa,
            List<Milhas> milhasList,
            int totalEntries
    ) {
        public ConsultarMilhasPorProgramaResult {
            Objects.requireNonNull(programa, "programa eh obrigatorio");
            Objects.requireNonNull(milhasList, "milhasList eh obrigatorio");
            milhasList = List.copyOf(milhasList); // defensive copy
        }
    }

    /**
     * Result object containing the total miles balance for a client.
     *
     * @param clienteId the client ID
     * @param saldoTotal the total miles balance across all programs
     */
    record SaldoTotalResult(
            UUID clienteId,
            int saldoTotal
    ) {
        public SaldoTotalResult {
            Objects.requireNonNull(clienteId, "clienteId eh obrigatorio");
        }
    }

    /**
     * Result object containing the average cost per thousand miles.
     *
     * @param clienteId the client ID
     * @param custoMedioMilheiro the weighted average cost per 1000 miles
     */
    record CustoMedioMilheiroResult(
            UUID clienteId,
            BigDecimal custoMedioMilheiro
    ) {
        public CustoMedioMilheiroResult {
            Objects.requireNonNull(clienteId, "clienteId eh obrigatorio");
            Objects.requireNonNull(custoMedioMilheiro, "custoMedioMilheiro eh obrigatorio");
        }
    }
}
