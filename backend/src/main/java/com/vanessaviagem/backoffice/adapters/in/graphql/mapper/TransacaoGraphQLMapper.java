package com.vanessaviagem.backoffice.adapters.in.graphql.mapper;

import com.vanessaviagem.backoffice.application.ports.in.RegistrarTransacaoUseCase.RegistrarCompraCommand;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarTransacaoUseCase.RegistrarBonusCommand;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarTransacaoUseCase.RegistrarVendaCommand;
import com.vanessaviagem.backoffice.domain.model.Transacao;
import com.vanessaviagem.backoffice.domain.model.enums.TipoTransacao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Mapper for converting between Transacao domain entity and GraphQL representations.
 *
 * <p>This mapper centralizes all GraphQL-layer transformations for Transacao,
 * ensuring consistent formatting and field mapping for API responses.</p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Domain → GraphQL DTO (for queries)</li>
 *   <li>GraphQL Input → Command (for mutations)</li>
 *   <li>Formatting of monetary values and dates for display</li>
 * </ul>
 */
public final class TransacaoGraphQLMapper {

    private static final int SCALE_DISPLAY = 2;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private TransacaoGraphQLMapper() {
    }

    /**
     * Converts a domain Transacao to a DTO record for GraphQL response.
     *
     * @param transacao the domain entity
     * @return TransacaoDTO record
     */
    public static TransacaoDTO toDTO(Transacao transacao) {
        return new TransacaoDTO(
                transacao.id().toString(),
                transacao.contaProgramaId().toString(),
                transacao.tipo().name(),
                transacao.milhas(),
                formatarMonetario(transacao.valorBRL()),
                transacao.fonte(),
                transacao.observacao(),
                transacao.data().format(DATE_FORMATTER),
                transacao.criadoEm().format(DATE_FORMATTER)
        );
    }

    /**
     * Converts a list of domain entities to DTOs.
     *
     * @param transacoes list of domain entities
     * @return list of DTOs
     */
    public static List<TransacaoDTO> toDTOList(List<Transacao> transacoes) {
        return transacoes.stream()
                .map(TransacaoGraphQLMapper::toDTO)
                .toList();
    }

    /**
     * Converts a GraphQL input to a RegistrarCompraCommand.
     *
     * @param input the GraphQL input
     * @param tenantId the tenant identifier
     * @return the domain command
     */
    public static RegistrarCompraCommand toCompraCommand(CompraInput input, UUID tenantId) {
        return new RegistrarCompraCommand(
                tenantId,
                UUID.fromString(input.programaId()),
                input.programaNome(),
                input.owner(),
                input.milhas(),
                new BigDecimal(input.valor()),
                input.fonte(),
                input.observacao()
        );
    }

    /**
     * Converts a GraphQL input to a RegistrarBonusCommand.
     *
     * @param input the GraphQL input
     * @param tenantId the tenant identifier
     * @return the domain command
     */
    public static RegistrarBonusCommand toBonusCommand(BonusInput input, UUID tenantId) {
        return new RegistrarBonusCommand(
                tenantId,
                UUID.fromString(input.programaId()),
                input.programaNome(),
                input.owner(),
                input.milhas(),
                input.fonte(),
                input.observacao()
        );
    }

    /**
     * Converts a GraphQL input to a RegistrarVendaCommand.
     *
     * @param input the GraphQL input
     * @param tenantId the tenant identifier
     * @return the domain command
     */
    public static RegistrarVendaCommand toVendaCommand(VendaInput input, UUID tenantId) {
        return new RegistrarVendaCommand(
                tenantId,
                UUID.fromString(input.programaId()),
                input.programaNome(),
                input.owner(),
                input.milhas(),
                new BigDecimal(input.valorVenda()),
                input.observacao()
        );
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
    public record TransacaoDTO(
            String id,
            String contaProgramaId,
            String tipo,
            long milhas,
            String valorBRL,
            String fonte,
            String observacao,
            String data,
            String criadoEm
    ) {}

    /**
     * Input record for registering a COMPRA (purchase) transaction.
     */
    public record CompraInput(
            String programaId,
            String programaNome,
            String owner,
            long milhas,
            String valor,
            String fonte,
            String observacao
    ) {}

    /**
     * Input record for registering a BONUS transaction.
     */
    public record BonusInput(
            String programaId,
            String programaNome,
            String owner,
            long milhas,
            String fonte,
            String observacao
    ) {}

    /**
     * Input record for registering a VENDA (sale) transaction.
     */
    public record VendaInput(
            String programaId,
            String programaNome,
            String owner,
            long milhas,
            String valorVenda,
            String observacao
    ) {}

    /**
     * Result record for sale transactions, including profit calculation.
     */
    public record VendaResultDTO(
            TransacaoDTO transacao,
            ContaProgramaGraphQLMapper.ContaProgramaDTO contaAtualizada,
            String lucro
    ) {
        public static VendaResultDTO from(
                Transacao transacao,
                com.vanessaviagem.backoffice.domain.model.ContaPrograma conta,
                BigDecimal lucro
        ) {
            return new VendaResultDTO(
                    toDTO(transacao),
                    ContaProgramaGraphQLMapper.toDTO(conta),
                    lucro.setScale(SCALE_DISPLAY, RoundingMode.HALF_UP).toPlainString()
            );
        }
    }
}
