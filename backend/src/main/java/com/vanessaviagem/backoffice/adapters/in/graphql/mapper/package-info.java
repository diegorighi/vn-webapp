/**
 * GraphQL mappers for transforming between domain entities and API representations.
 *
 * <p>This package contains mapper classes that centralize the conversion logic
 * between domain models and GraphQL DTOs/Inputs:</p>
 * <ul>
 *   <li>{@link com.vanessaviagem.backoffice.adapters.in.graphql.mapper.ContaProgramaGraphQLMapper}
 *       - Maps ContaPrograma between domain and GraphQL</li>
 *   <li>{@link com.vanessaviagem.backoffice.adapters.in.graphql.mapper.TransacaoGraphQLMapper}
 *       - Maps Transacao between domain and GraphQL</li>
 * </ul>
 *
 * <p>Benefits:</p>
 * <ul>
 *   <li>Single point of change for API contract modifications</li>
 *   <li>Clear separation between domain and presentation concerns</li>
 *   <li>Consistent formatting of monetary values and dates</li>
 *   <li>Type-safe Input → Command conversions</li>
 *   <li>Easier testing of mapping logic in isolation</li>
 * </ul>
 *
 * <p>Usage pattern:</p>
 * <pre>{@code
 * // In resolver:
 * @QueryMapping
 * public ContaProgramaDTO conta(@Argument String id) {
 *     ContaPrograma domain = service.buscarPorId(UUID.fromString(id)).orElseThrow();
 *     return ContaProgramaGraphQLMapper.toDTO(domain);
 * }
 *
 * @MutationMapping
 * public TransacaoDTO registrarCompra(@Argument CompraInput input) {
 *     UUID tenantId = TenantContext.current().tenantId();
 *     RegistrarCompraCommand command = TransacaoGraphQLMapper.toCompraCommand(input, tenantId);
 *     TransacaoResult result = service.registrarCompra(command);
 *     return TransacaoGraphQLMapper.toDTO(result.transacao());
 * }
 * }</pre>
 */
package com.vanessaviagem.backoffice.adapters.in.graphql.mapper;
