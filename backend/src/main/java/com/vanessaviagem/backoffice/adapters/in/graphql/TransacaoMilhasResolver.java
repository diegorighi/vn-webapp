package com.vanessaviagem.backoffice.adapters.in.graphql;

import com.vanessaviagem.backoffice.application.ports.in.ConsultarContaProgramaUseCase;
import com.vanessaviagem.backoffice.application.ports.in.ConsultarTransacoesUseCase;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarTransacaoUseCase;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarTransacaoUseCase.RegistrarBonusCommand;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarTransacaoUseCase.RegistrarCompraCommand;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarTransacaoUseCase.RegistrarVendaCommand;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarTransacaoUseCase.TransacaoResult;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarTransacaoUseCase.VendaResult;
import com.vanessaviagem.backoffice.domain.model.ContaPrograma;
import com.vanessaviagem.backoffice.domain.model.TenantContext;
import com.vanessaviagem.backoffice.domain.model.Transacao;
import com.vanessaviagem.backoffice.domain.model.enums.TipoTransacao;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * GraphQL resolver for ContaPrograma and Transacao operations.
 *
 * <p>This adapter exposes the application use cases through GraphQL queries and mutations.
 * It handles input transformation and response mapping between GraphQL types and domain objects.</p>
 *
 * <p>All operations are scoped to the current tenant via TenantContext.</p>
 */
@Controller
public class TransacaoMilhasResolver {

    private final RegistrarTransacaoUseCase registrarTransacaoUseCase;
    private final ConsultarContaProgramaUseCase consultarContaProgramaUseCase;
    private final ConsultarTransacoesUseCase consultarTransacoesUseCase;

    public TransacaoMilhasResolver(
            RegistrarTransacaoUseCase registrarTransacaoUseCase,
            ConsultarContaProgramaUseCase consultarContaProgramaUseCase,
            ConsultarTransacoesUseCase consultarTransacoesUseCase
    ) {
        this.registrarTransacaoUseCase = Objects.requireNonNull(registrarTransacaoUseCase,
                "registrarTransacaoUseCase eh obrigatorio");
        this.consultarContaProgramaUseCase = Objects.requireNonNull(consultarContaProgramaUseCase,
                "consultarContaProgramaUseCase eh obrigatorio");
        this.consultarTransacoesUseCase = Objects.requireNonNull(consultarTransacoesUseCase,
                "consultarTransacoesUseCase eh obrigatorio");
    }

    // ==================== QUERIES - Contas de Programa ====================

    /**
     * Lists all program accounts for the current tenant.
     *
     * @return list of all ContaPrograma entries
     */
    @QueryMapping
    public List<ContaPrograma> contasPrograma() {
        UUID tenantId = TenantContext.current().tenantId();
        return consultarContaProgramaUseCase.listarTodos(tenantId);
    }

    /**
     * Lists program accounts for a specific owner within the current tenant.
     *
     * @param owner the account owner name
     * @return list of ContaPrograma entries for the owner
     */
    @QueryMapping
    public List<ContaPrograma> contasProgramaPorOwner(@Argument String owner) {
        Objects.requireNonNull(owner, "owner eh obrigatorio");
        UUID tenantId = TenantContext.current().tenantId();
        return consultarContaProgramaUseCase.listarPorOwner(tenantId, owner);
    }

    /**
     * Finds a specific program account by ID.
     *
     * @param id the account ID
     * @return the ContaPrograma if found, null otherwise
     */
    @QueryMapping
    public ContaPrograma contaPrograma(@Argument String id) {
        Objects.requireNonNull(id, "id eh obrigatorio");
        UUID tenantId = TenantContext.current().tenantId();
        UUID uuid = UUID.fromString(id);
        return consultarContaProgramaUseCase.buscarPorId(tenantId, uuid).orElse(null);
    }

    /**
     * Returns a summary of all accounts for the current tenant.
     *
     * @return ResumoContas containing totals and account list
     */
    @QueryMapping
    public ResumoContas resumoContas() {
        UUID tenantId = TenantContext.current().tenantId();

        List<ContaPrograma> contas = consultarContaProgramaUseCase.listarTodos(tenantId);
        long totalGeral = consultarContaProgramaUseCase.totalMilhas(tenantId);
        Map<String, Long> porOwner = consultarContaProgramaUseCase.totaisPorOwner(tenantId);
        Map<String, Long> porPrograma = consultarContaProgramaUseCase.totaisPorPrograma(tenantId);

        List<TotalPorOwner> totaisOwner = porOwner.entrySet().stream()
                .map(e -> new TotalPorOwner(e.getKey(), e.getValue()))
                .toList();

        List<TotalPorPrograma> totaisPrograma = porPrograma.entrySet().stream()
                .map(e -> new TotalPorPrograma(e.getKey(), e.getValue()))
                .toList();

        return new ResumoContas(
                totalGeral,
                contas.size(),
                totaisOwner,
                totaisPrograma,
                contas
        );
    }

    // ==================== QUERIES - Transacoes ====================

    /**
     * Lists all transactions for a specific program account.
     *
     * @param contaProgramaId the account ID
     * @return list of TransacaoDTO entries ordered by date descending
     */
    @QueryMapping
    public List<TransacaoDTO> transacoes(@Argument String contaProgramaId) {
        Objects.requireNonNull(contaProgramaId, "contaProgramaId eh obrigatorio");
        UUID uuid = UUID.fromString(contaProgramaId);
        return consultarTransacoesUseCase.listarPorContaPrograma(uuid)
                .stream()
                .map(this::mapTransacaoToDTO)
                .toList();
    }

    /**
     * Lists transactions for a specific period.
     *
     * @param contaProgramaId the account ID
     * @param inicio start of the period (inclusive)
     * @param fim end of the period (inclusive)
     * @return list of TransacaoDTO entries within the period
     */
    @QueryMapping
    public List<TransacaoDTO> transacoesPorPeriodo(
            @Argument String contaProgramaId,
            @Argument LocalDateTime inicio,
            @Argument LocalDateTime fim
    ) {
        Objects.requireNonNull(contaProgramaId, "contaProgramaId eh obrigatorio");
        Objects.requireNonNull(inicio, "inicio eh obrigatorio");
        Objects.requireNonNull(fim, "fim eh obrigatorio");

        UUID uuid = UUID.fromString(contaProgramaId);
        return consultarTransacoesUseCase.listarPorPeriodo(uuid, inicio, fim)
                .stream()
                .map(this::mapTransacaoToDTO)
                .toList();
    }

    /**
     * Lists transactions of a specific type.
     *
     * @param contaProgramaId the account ID
     * @param tipo the transaction type (COMPRA, VENDA, or BONUS)
     * @return list of TransacaoDTO entries of the specified type
     */
    @QueryMapping
    public List<TransacaoDTO> transacoesPorTipo(
            @Argument String contaProgramaId,
            @Argument TipoTransacao tipo
    ) {
        Objects.requireNonNull(contaProgramaId, "contaProgramaId eh obrigatorio");
        Objects.requireNonNull(tipo, "tipo eh obrigatorio");

        UUID uuid = UUID.fromString(contaProgramaId);
        return consultarTransacoesUseCase.listarPorTipo(uuid, tipo)
                .stream()
                .map(this::mapTransacaoToDTO)
                .toList();
    }

    // ==================== MUTATIONS ====================

    /**
     * Registers a purchase of miles.
     * Creates the account if it doesn't exist.
     *
     * @param input the purchase data
     * @return ResultadoCompraDTO with the created transaction and updated account
     */
    @MutationMapping
    public ResultadoCompraDTO registrarCompra(@Argument RegistrarCompraInput input) {
        Objects.requireNonNull(input, "input eh obrigatorio");

        UUID tenantId = TenantContext.current().tenantId();
        UUID programaId = UUID.fromString(input.programaId());

        RegistrarCompraCommand command = new RegistrarCompraCommand(
                tenantId,
                programaId,
                input.programaNome(),
                input.owner(),
                input.milhas(),
                input.valor(),
                input.fonte(),
                input.observacao()
        );

        TransacaoResult result = registrarTransacaoUseCase.registrarCompra(command);

        return new ResultadoCompraDTO(
                mapTransacaoToDTO(result.transacao()),
                result.contaAtualizada()
        );
    }

    /**
     * Registers a bonus of miles (cashback, promotions).
     * Creates the account if it doesn't exist.
     *
     * @param input the bonus data
     * @return ResultadoCompraDTO with the created transaction and updated account
     */
    @MutationMapping
    public ResultadoCompraDTO registrarBonus(@Argument RegistrarBonusInput input) {
        Objects.requireNonNull(input, "input eh obrigatorio");

        UUID tenantId = TenantContext.current().tenantId();
        UUID programaId = UUID.fromString(input.programaId());

        RegistrarBonusCommand command = new RegistrarBonusCommand(
                tenantId,
                programaId,
                input.programaNome(),
                input.owner(),
                input.milhas(),
                input.fonte(),
                input.observacao()
        );

        TransacaoResult result = registrarTransacaoUseCase.registrarBonus(command);

        return new ResultadoCompraDTO(
                mapTransacaoToDTO(result.transacao()),
                result.contaAtualizada()
        );
    }

    /**
     * Registers a sale of miles.
     * The account must exist and have sufficient balance.
     *
     * @param input the sale data
     * @return ResultadoVendaDTO with transaction, updated account, and profit
     */
    @MutationMapping
    public ResultadoVendaDTO registrarVenda(@Argument RegistrarVendaInput input) {
        Objects.requireNonNull(input, "input eh obrigatorio");

        UUID tenantId = TenantContext.current().tenantId();
        UUID programaId = UUID.fromString(input.programaId());

        RegistrarVendaCommand command = new RegistrarVendaCommand(
                tenantId,
                programaId,
                input.programaNome(),
                input.owner(),
                input.milhas(),
                input.valorVenda(),
                input.observacao()
        );

        VendaResult result = registrarTransacaoUseCase.registrarVenda(command);

        // Calculate custoRemovido as (valorVenda - lucro)
        BigDecimal custoRemovido = input.valorVenda().subtract(result.lucro());

        return new ResultadoVendaDTO(
                mapTransacaoToDTO(result.transacao()),
                result.contaAtualizada(),
                custoRemovido,
                result.lucro()
        );
    }

    // ==================== Private Helpers ====================

    /**
     * Maps a domain Transacao to a DTO for GraphQL response.
     * The DTO uses contaProgramaId (from GraphQL schema) instead of programaId (domain).
     */
    private TransacaoDTO mapTransacaoToDTO(Transacao transacao) {
        return new TransacaoDTO(
                transacao.id(),
                transacao.contaProgramaId(),
                transacao.tipo(),
                transacao.milhas(),
                transacao.valorBRL(),
                transacao.fonte(),
                transacao.observacao(),
                transacao.data(),
                transacao.criadoEm()
        );
    }

    // ==================== DTOs for GraphQL Input/Output ====================

    /**
     * Input for registering a purchase.
     */
    public record RegistrarCompraInput(
            String programaId,
            String programaNome,
            String owner,
            long milhas,
            BigDecimal valor,
            String fonte,
            String observacao
    ) {}

    /**
     * Input for registering a bonus.
     */
    public record RegistrarBonusInput(
            String programaId,
            String programaNome,
            String owner,
            long milhas,
            String fonte,
            String observacao
    ) {}

    /**
     * Input for registering a sale.
     */
    public record RegistrarVendaInput(
            String programaId,
            String programaNome,
            String owner,
            long milhas,
            BigDecimal valorVenda,
            String observacao
    ) {}

    /**
     * DTO representing a transaction for GraphQL.
     * Uses contaProgramaId to match the GraphQL schema field name.
     */
    public record TransacaoDTO(
            UUID id,
            UUID contaProgramaId,
            TipoTransacao tipo,
            long milhas,
            BigDecimal valorBRL,
            String fonte,
            String observacao,
            LocalDateTime data,
            LocalDateTime criadoEm
    ) {}

    /**
     * Result DTO for purchase and bonus operations.
     */
    public record ResultadoCompraDTO(
            TransacaoDTO transacao,
            ContaPrograma contaAtualizada
    ) {}

    /**
     * Result DTO for sale operations with profit information.
     */
    public record ResultadoVendaDTO(
            TransacaoDTO transacao,
            ContaPrograma contaAtualizada,
            BigDecimal custoRemovido,
            BigDecimal lucro
    ) {}

    /**
     * Summary of all accounts for a tenant.
     */
    public record ResumoContas(
            long totalGeral,
            int quantidadeContas,
            List<TotalPorOwner> porOwner,
            List<TotalPorPrograma> porPrograma,
            List<ContaPrograma> contas
    ) {}

    /**
     * Total miles for an owner.
     */
    public record TotalPorOwner(String owner, long total) {}

    /**
     * Total miles for a program.
     */
    public record TotalPorPrograma(String programa, long total) {}
}
