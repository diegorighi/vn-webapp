package com.vanessaviagem.backoffice.application.ports.in;

import com.vanessaviagem.backoffice.domain.model.ContaPrograma;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Input port for querying ContaPrograma entries.
 * This use case handles all read operations for program accounts.
 *
 * <p>Queries are scoped by tenant for multi-tenant isolation.
 * All methods are read-only and do not modify state.</p>
 */
public interface ConsultarContaProgramaUseCase {

    /**
     * Finds a ContaPrograma by its unique identifier within a tenant.
     *
     * @param tenantId the tenant identifier
     * @param id the account ID
     * @return an Optional containing the account if found, empty otherwise
     * @throws NullPointerException if any parameter is null
     */
    Optional<ContaPrograma> buscarPorId(UUID tenantId, UUID id);

    /**
     * Finds all ContaPrograma entries for a specific owner within a tenant.
     *
     * @param tenantId the tenant identifier
     * @param owner the account owner name
     * @return a list of accounts for the owner (never null, may be empty)
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if owner is blank
     */
    List<ContaPrograma> listarPorOwner(UUID tenantId, String owner);

    /**
     * Finds all ContaPrograma entries for a specific tenant.
     *
     * @param tenantId the tenant identifier
     * @return a list of all accounts for the tenant (never null, may be empty)
     * @throws NullPointerException if tenantId is null
     */
    List<ContaPrograma> listarTodos(UUID tenantId);

    /**
     * Calculates the total miles balance across all accounts for a tenant.
     *
     * @param tenantId the tenant identifier
     * @return the total miles balance
     * @throws NullPointerException if tenantId is null
     */
    long totalMilhas(UUID tenantId);

    /**
     * Calculates the total miles balance grouped by owner for a tenant.
     *
     * @param tenantId the tenant identifier
     * @return a map where keys are owner names and values are total miles
     * @throws NullPointerException if tenantId is null
     */
    Map<String, Long> totaisPorOwner(UUID tenantId);

    /**
     * Calculates the total miles balance grouped by program for a tenant.
     *
     * @param tenantId the tenant identifier
     * @return a map where keys are program names and values are total miles
     * @throws NullPointerException if tenantId is null
     */
    Map<String, Long> totaisPorPrograma(UUID tenantId);
}
