package com.vanessaviagem.backoffice.application.ports.in;

import com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhas;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Input port for querying loyalty programs.
 * This use case handles all read operations on programs.
 */
public interface ConsultarProgramaUseCase {

    /**
     * Finds a program by its unique identifier.
     *
     * @param id the program ID
     * @return an Optional containing the program if found, empty otherwise
     */
    Optional<ProgramaDeMilhas> buscarPorId(UUID id);

    /**
     * Finds a program by its brand name.
     *
     * @param brand the brand name to search for
     * @return an Optional containing the program if found, empty otherwise
     */
    Optional<ProgramaDeMilhas> buscarPorBrand(String brand);

    /**
     * Lists all loyalty programs.
     *
     * @return a list of all programs (may be empty)
     */
    List<ProgramaDeMilhas> listarTodos();

    /**
     * Lists only active loyalty programs.
     *
     * @return a list of active programs (may be empty)
     */
    List<ProgramaDeMilhas> listarAtivos();

    /**
     * Query object for searching programs with filters.
     *
     * @param apenasAtivos if true, returns only active programs
     * @param brandContains filter by brand name containing this string (case-insensitive)
     */
    record ConsultarProgramaQuery(
            boolean apenasAtivos,
            String brandContains
    ) {
        /**
         * Creates a query for all programs.
         *
         * @return a query with no filters
         */
        public static ConsultarProgramaQuery todos() {
            return new ConsultarProgramaQuery(false, null);
        }

        /**
         * Creates a query for active programs only.
         *
         * @return a query filtering active programs
         */
        public static ConsultarProgramaQuery somenteAtivos() {
            return new ConsultarProgramaQuery(true, null);
        }

        /**
         * Creates a query filtering by brand name.
         *
         * @param brandContains the string to search in brand names
         * @return a query with brand filter
         */
        public static ConsultarProgramaQuery comBrand(String brandContains) {
            Objects.requireNonNull(brandContains, "brandContains eh obrigatorio");
            return new ConsultarProgramaQuery(false, brandContains);
        }
    }
}
