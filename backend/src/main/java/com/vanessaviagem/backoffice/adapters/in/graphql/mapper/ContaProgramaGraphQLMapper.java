package com.vanessaviagem.backoffice.adapters.in.graphql.mapper;

import com.vanessaviagem.backoffice.domain.model.ContaPrograma;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Mapper for converting between ContaPrograma domain entity and GraphQL representations.
 *
 * <p>This mapper centralizes all GraphQL-layer transformations for ContaPrograma,
 * ensuring consistent formatting and field mapping for API responses.</p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Domain → GraphQL DTO (for queries)</li>
 *   <li>GraphQL Input → Domain/Command (for mutations)</li>
 *   <li>Formatting of monetary values and dates for display</li>
 * </ul>
 */
public final class ContaProgramaGraphQLMapper {

    private static final int SCALE_DISPLAY = 2;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private ContaProgramaGraphQLMapper() {
    }

    /**
     * Converts a domain ContaPrograma to a GraphQL-friendly map representation.
     *
     * @param conta the domain entity
     * @return map with formatted fields for GraphQL response
     */
    public static Map<String, Object> toGraphQL(ContaPrograma conta) {
        return Map.of(
                "id", conta.id().toString(),
                "tenantId", conta.tenantId().toString(),
                "programaId", conta.programaId().toString(),
                "programaNome", conta.programaNome(),
                "owner", conta.owner(),
                "saldoMilhas", conta.saldoMilhas(),
                "custoBaseTotalBRL", formatarMonetario(conta.custoBaseTotalBRL()),
                "custoMedioMilheiroAtual", formatarMonetario(conta.custoMedioMilheiroAtual()),
                "criadoEm", conta.criadoEm().format(DATE_FORMATTER),
                "atualizadoEm", conta.atualizadoEm().format(DATE_FORMATTER)
        );
    }

    /**
     * Converts a domain ContaPrograma to a DTO record for GraphQL response.
     *
     * @param conta the domain entity
     * @return ContaProgramaDTO record
     */
    public static ContaProgramaDTO toDTO(ContaPrograma conta) {
        return new ContaProgramaDTO(
                conta.id().toString(),
                conta.tenantId().toString(),
                conta.programaId().toString(),
                conta.programaNome(),
                conta.owner(),
                conta.saldoMilhas(),
                formatarMonetario(conta.custoBaseTotalBRL()),
                formatarMonetario(conta.custoMedioMilheiroAtual()),
                conta.criadoEm().format(DATE_FORMATTER),
                conta.atualizadoEm().format(DATE_FORMATTER)
        );
    }

    /**
     * Converts a list of domain entities to DTOs.
     *
     * @param contas list of domain entities
     * @return list of DTOs
     */
    public static List<ContaProgramaDTO> toDTOList(List<ContaPrograma> contas) {
        return contas.stream()
                .map(ContaProgramaGraphQLMapper::toDTO)
                .toList();
    }

    /**
     * Formats a BigDecimal monetary value for display with 2 decimal places.
     *
     * @param valor the monetary value
     * @return formatted string representation
     */
    private static String formatarMonetario(BigDecimal valor) {
        if (valor == null) {
            return "0.00";
        }
        return valor.setScale(SCALE_DISPLAY, RoundingMode.HALF_UP).toPlainString();
    }

    /**
     * DTO record for GraphQL responses.
     */
    public record ContaProgramaDTO(
            String id,
            String tenantId,
            String programaId,
            String programaNome,
            String owner,
            long saldoMilhas,
            String custoBaseTotalBRL,
            String custoMedioMilheiroAtual,
            String criadoEm,
            String atualizadoEm
    ) {}

    /**
     * Input record for creating/updating ContaPrograma via GraphQL mutations.
     */
    public record ContaProgramaInput(
            String programaId,
            String programaNome,
            String owner
    ) {}

    /**
     * Summary record for aggregated views.
     */
    public record ContaProgramaResumo(
            String programaNome,
            String owner,
            long saldoMilhas,
            String custoMedioMilheiroAtual
    ) {
        public static ContaProgramaResumo from(ContaPrograma conta) {
            return new ContaProgramaResumo(
                    conta.programaNome(),
                    conta.owner(),
                    conta.saldoMilhas(),
                    conta.custoMedioMilheiroAtual()
                            .setScale(SCALE_DISPLAY, RoundingMode.HALF_UP)
                            .toPlainString()
            );
        }
    }
}
