package com.vanessaviagem.backoffice.application.services;

import com.vanessaviagem.backoffice.application.ports.in.ConsultarContaProgramaUseCase;
import com.vanessaviagem.backoffice.application.ports.in.ConsultarTransacoesUseCase;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarTransacaoUseCase;
import com.vanessaviagem.backoffice.application.ports.out.ContaProgramaRepository;
import com.vanessaviagem.backoffice.application.ports.out.TransacaoRepository;
import com.vanessaviagem.backoffice.domain.model.ContaPrograma;
import com.vanessaviagem.backoffice.domain.model.ResultadoVenda;
import com.vanessaviagem.backoffice.domain.model.TenantContext;
import com.vanessaviagem.backoffice.domain.model.Transacao;
import com.vanessaviagem.backoffice.domain.model.enums.TipoTransacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Application service that orchestrates transaction operations on program accounts.
 *
 * <p>This service implements the use cases for registering transactions (purchases,
 * bonuses, and sales) and querying both accounts and transaction history.</p>
 *
 * <p>All operations are scoped to the current tenant via TenantContext.</p>
 */
@Service
public class TransacaoMilhasService implements
        RegistrarTransacaoUseCase,
        ConsultarContaProgramaUseCase,
        ConsultarTransacoesUseCase {

    private static final Logger LOG = LoggerFactory.getLogger(TransacaoMilhasService.class);

    private final ContaProgramaRepository contaProgramaRepository;
    private final TransacaoRepository transacaoRepository;

    public TransacaoMilhasService(
            ContaProgramaRepository contaProgramaRepository,
            TransacaoRepository transacaoRepository
    ) {
        this.contaProgramaRepository = Objects.requireNonNull(contaProgramaRepository, "contaProgramaRepository eh obrigatorio");
        this.transacaoRepository = Objects.requireNonNull(transacaoRepository, "transacaoRepository eh obrigatorio");
    }

    // ==================== RegistrarTransacaoUseCase ====================

    @Override
    @Transactional
    public TransacaoResult registrarCompra(RegistrarCompraCommand command) {
        Objects.requireNonNull(command, "command eh obrigatorio");

        LOG.info("Registrando compra: programa={}, owner={}, milhas={}, valor={}",
                command.programaNome(), command.owner(), command.milhas(), command.valor());

        ContaPrograma conta = obterOuCriarConta(
                command.tenantId(),
                command.programaId(),
                command.programaNome(),
                command.owner()
        );

        ContaPrograma contaAtualizada = conta.aplicarCompra(command.milhas(), command.valor());
        contaAtualizada = contaProgramaRepository.atualizar(contaAtualizada);

        Transacao transacao = Transacao.criarCompra(
                contaAtualizada.id(),
                command.milhas(),
                command.valor(),
                command.fonte(),
                command.observacao()
        );
        transacao = transacaoRepository.salvar(transacao);

        LOG.info("Compra registrada: transacaoId={}, contaId={}, novoSaldo={}",
                transacao.id(), contaAtualizada.id(), contaAtualizada.saldoMilhas());

        return new TransacaoResult(transacao, contaAtualizada);
    }

    @Override
    @Transactional
    public TransacaoResult registrarBonus(RegistrarBonusCommand command) {
        Objects.requireNonNull(command, "command eh obrigatorio");

        LOG.info("Registrando bonus: programa={}, owner={}, milhas={}, fonte={}",
                command.programaNome(), command.owner(), command.milhas(), command.fonte());

        ContaPrograma conta = obterOuCriarConta(
                command.tenantId(),
                command.programaId(),
                command.programaNome(),
                command.owner()
        );

        ContaPrograma contaAtualizada = conta.aplicarBonus(command.milhas());
        contaAtualizada = contaProgramaRepository.atualizar(contaAtualizada);

        Transacao transacao = Transacao.criarBonus(
                contaAtualizada.id(),
                command.milhas(),
                command.fonte(),
                command.observacao()
        );
        transacao = transacaoRepository.salvar(transacao);

        LOG.info("Bonus registrado: transacaoId={}, contaId={}, novoSaldo={}",
                transacao.id(), contaAtualizada.id(), contaAtualizada.saldoMilhas());

        return new TransacaoResult(transacao, contaAtualizada);
    }

    @Override
    @Transactional
    public VendaResult registrarVenda(RegistrarVendaCommand command) {
        Objects.requireNonNull(command, "command eh obrigatorio");

        LOG.info("Registrando venda: programa={}, owner={}, milhas={}, valorVenda={}",
                command.programaNome(), command.owner(), command.milhas(), command.valorVenda());

        Optional<ContaPrograma> contaOpt = contaProgramaRepository.buscarPorTenantProgramaEOwner(
                command.tenantId(),
                command.programaId(),
                command.owner()
        );

        if (contaOpt.isEmpty()) {
            throw new IllegalStateException(
                    "ContaPrograma nao encontrada para programa=" + command.programaNome()
                            + " e owner=" + command.owner()
            );
        }

        ContaPrograma conta = contaOpt.get();
        ResultadoVenda resultado = conta.aplicarVenda(command.milhas(), command.valorVenda());

        ContaPrograma contaAtualizada = contaProgramaRepository.atualizar(resultado.contaAtualizada());

        Transacao transacao = Transacao.criarVenda(
                contaAtualizada.id(),
                command.milhas(),
                command.valorVenda(),
                command.observacao()
        );
        transacao = transacaoRepository.salvar(transacao);

        LOG.info("Venda registrada: transacaoId={}, contaId={}, novoSaldo={}, lucro={}",
                transacao.id(), contaAtualizada.id(), contaAtualizada.saldoMilhas(), resultado.lucro());

        return new VendaResult(transacao, contaAtualizada, resultado.lucro());
    }

    // ==================== ConsultarContaProgramaUseCase ====================

    @Override
    @Transactional(readOnly = true)
    public Optional<ContaPrograma> buscarPorId(UUID tenantId, UUID id) {
        Objects.requireNonNull(tenantId, "tenantId eh obrigatorio");
        Objects.requireNonNull(id, "id eh obrigatorio");
        return contaProgramaRepository.buscarPorId(tenantId, id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContaPrograma> listarPorOwner(UUID tenantId, String owner) {
        Objects.requireNonNull(tenantId, "tenantId eh obrigatorio");
        Objects.requireNonNull(owner, "owner eh obrigatorio");
        if (owner.isBlank()) {
            throw new IllegalArgumentException("owner nao pode estar vazio");
        }
        return contaProgramaRepository.buscarPorOwner(tenantId, owner);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContaPrograma> listarTodos(UUID tenantId) {
        Objects.requireNonNull(tenantId, "tenantId eh obrigatorio");
        return contaProgramaRepository.buscarTodos(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public long totalMilhas(UUID tenantId) {
        Objects.requireNonNull(tenantId, "tenantId eh obrigatorio");
        return contaProgramaRepository.calcularTotalMilhas(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> totaisPorOwner(UUID tenantId) {
        Objects.requireNonNull(tenantId, "tenantId eh obrigatorio");
        return contaProgramaRepository.calcularTotaisPorOwner(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> totaisPorPrograma(UUID tenantId) {
        Objects.requireNonNull(tenantId, "tenantId eh obrigatorio");
        return contaProgramaRepository.calcularTotaisPorPrograma(tenantId);
    }

    // ==================== ConsultarTransacoesUseCase ====================

    @Override
    @Transactional(readOnly = true)
    public List<Transacao> listarPorContaPrograma(UUID contaProgramaId) {
        Objects.requireNonNull(contaProgramaId, "contaProgramaId eh obrigatorio");
        return transacaoRepository.buscarPorContaPrograma(contaProgramaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transacao> listarPorPeriodo(UUID contaProgramaId, LocalDateTime inicio, LocalDateTime fim) {
        Objects.requireNonNull(contaProgramaId, "contaProgramaId eh obrigatorio");
        Objects.requireNonNull(inicio, "inicio eh obrigatorio");
        Objects.requireNonNull(fim, "fim eh obrigatorio");
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("inicio nao pode ser posterior a fim");
        }
        return transacaoRepository.buscarPorPeriodo(contaProgramaId, inicio, fim);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transacao> listarPorTipo(UUID contaProgramaId, TipoTransacao tipo) {
        Objects.requireNonNull(contaProgramaId, "contaProgramaId eh obrigatorio");
        Objects.requireNonNull(tipo, "tipo eh obrigatorio");
        return transacaoRepository.buscarPorTipo(contaProgramaId, tipo);
    }

    // ==================== Private Helpers ====================

    /**
     * Obtains an existing account or creates a new one if it doesn't exist.
     * Used for COMPRA and BONUS operations which can create accounts automatically.
     *
     * @param tenantId the tenant identifier
     * @param programaId the program identifier
     * @param programaNome the program name (for denormalization)
     * @param owner the account owner
     * @return the existing or newly created account
     */
    private ContaPrograma obterOuCriarConta(UUID tenantId, UUID programaId, String programaNome, String owner) {
        Optional<ContaPrograma> contaOpt = contaProgramaRepository.buscarPorTenantProgramaEOwner(
                tenantId, programaId, owner
        );

        if (contaOpt.isPresent()) {
            LOG.debug("Conta existente encontrada: id={}", contaOpt.get().id());
            return contaOpt.get();
        }

        LOG.info("Criando nova conta: programa={}, owner={}", programaNome, owner);
        ContaPrograma novaConta = ContaPrograma.criar(tenantId, programaId, programaNome, owner);
        return contaProgramaRepository.salvar(novaConta);
    }
}
