package com.vanessaviagem.backoffice.application.services;

import com.vanessaviagem.backoffice.application.ports.in.AtualizarMilhasUseCase;
import com.vanessaviagem.backoffice.application.ports.in.ConsultarMilhasUseCase;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarMilhasUseCase;
import com.vanessaviagem.backoffice.application.ports.in.RemoverMilhasUseCase;
import com.vanessaviagem.backoffice.application.ports.out.MilhasRepository;
import com.vanessaviagem.backoffice.domain.exceptions.MilhasNaoEncontradaException;
import com.vanessaviagem.backoffice.domain.model.Milhas;
import com.vanessaviagem.backoffice.domain.model.TenantContext;
import com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for managing miles (milhas) operations.
 * This service implements all miles-related use cases and orchestrates
 * the interactions between the domain and persistence layers.
 *
 * <p>All methods use TenantContext for multi-tenant isolation.</p>
 */
@Service
public class MilhasService implements RegistrarMilhasUseCase, AtualizarMilhasUseCase,
        ConsultarMilhasUseCase, RemoverMilhasUseCase {

    private static final int MILHEIRO = 1000;
    private static final int CUSTO_MEDIO_SCALE = 4;

    private final MilhasRepository milhasRepository;

    public MilhasService(MilhasRepository milhasRepository) {
        this.milhasRepository = Objects.requireNonNull(milhasRepository, "milhasRepository eh obrigatorio");
    }

    private UUID currentTenantId() {
        return TenantContext.current().accountId();
    }

    // --- RegistrarMilhasUseCase Implementation ---

    @Override
    @Transactional
    public RegistrarMilhasResult execute(RegistrarMilhasCommand command) {
        Objects.requireNonNull(command, "command eh obrigatorio");

        Milhas milhas = command.toMilhas();
        Milhas saved = milhasRepository.salvar(currentTenantId(), command.clienteId(), milhas);

        return RegistrarMilhasResult.from(saved);
    }

    // --- AtualizarMilhasUseCase Implementation ---

    @Override
    @Transactional
    public AtualizarMilhasResult execute(AtualizarMilhasCommand command) {
        Objects.requireNonNull(command, "command eh obrigatorio");

        UUID tenantId = currentTenantId();
        Milhas existing = milhasRepository.buscarPorId(tenantId, command.milhasId())
                .orElseThrow(() -> new MilhasNaoEncontradaException(command.milhasId()));

        Milhas updated = command.applyTo(existing);
        Milhas saved = milhasRepository.atualizar(tenantId, updated);

        return AtualizarMilhasResult.from(saved);
    }

    // --- RemoverMilhasUseCase Implementation ---

    @Override
    @Transactional
    public void execute(RemoverMilhasCommand command) {
        Objects.requireNonNull(command, "command eh obrigatorio");

        UUID tenantId = currentTenantId();
        if (!milhasRepository.existePorId(tenantId, command.milhasId())) {
            throw new MilhasNaoEncontradaException(command.milhasId());
        }

        milhasRepository.excluir(tenantId, command.milhasId());
    }

    // --- ConsultarMilhasUseCase Implementation ---

    @Override
    @Transactional(readOnly = true)
    public Optional<ConsultarMilhasResult> buscarPorId(ConsultarMilhasPorIdQuery query) {
        Objects.requireNonNull(query, "query eh obrigatorio");

        return milhasRepository.buscarPorId(currentTenantId(), query.milhasId())
                .map(ConsultarMilhasResult::from);
    }

    @Override
    @Transactional(readOnly = true)
    public ConsultarMilhasPorClienteResult buscarPorCliente(ConsultarMilhasPorClienteQuery query) {
        Objects.requireNonNull(query, "query eh obrigatorio");

        List<Milhas> milhasList = milhasRepository.buscarPorCliente(currentTenantId(), query.clienteId());

        return new ConsultarMilhasPorClienteResult(
                query.clienteId(),
                milhasList,
                milhasList.size()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ConsultarMilhasPorProgramaResult buscarPorPrograma(ConsultarMilhasPorProgramaQuery query) {
        Objects.requireNonNull(query, "query eh obrigatorio");

        List<Milhas> milhasList = milhasRepository.buscarPorPrograma(currentTenantId(), query.programa());

        return new ConsultarMilhasPorProgramaResult(
                query.programa(),
                milhasList,
                milhasList.size()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SaldoTotalResult calcularSaldoTotal(ConsultarMilhasPorClienteQuery query) {
        Objects.requireNonNull(query, "query eh obrigatorio");

        List<Milhas> milhasList = milhasRepository.buscarPorCliente(currentTenantId(), query.clienteId());

        int saldoTotal = milhasList.stream()
                .mapToInt(Milhas::quantidade)
                .sum();

        return new SaldoTotalResult(query.clienteId(), saldoTotal);
    }

    @Override
    @Transactional(readOnly = true)
    public CustoMedioMilheiroResult calcularCustoMedioMilheiro(ConsultarMilhasPorClienteQuery query) {
        Objects.requireNonNull(query, "query eh obrigatorio");

        List<Milhas> milhasList = milhasRepository.buscarPorCliente(currentTenantId(), query.clienteId());

        if (milhasList.isEmpty()) {
            return new CustoMedioMilheiroResult(query.clienteId(), BigDecimal.ZERO);
        }

        BigDecimal totalValor = milhasList.stream()
                .map(Milhas::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalQuantidade = milhasList.stream()
                .mapToInt(Milhas::quantidade)
                .sum();

        if (totalQuantidade == 0) {
            return new CustoMedioMilheiroResult(query.clienteId(), BigDecimal.ZERO);
        }

        BigDecimal totalMilheiros = BigDecimal.valueOf(totalQuantidade)
                .divide(BigDecimal.valueOf(MILHEIRO), CUSTO_MEDIO_SCALE + 2, RoundingMode.HALF_UP);

        BigDecimal custoMedio = totalValor.divide(totalMilheiros, CUSTO_MEDIO_SCALE, RoundingMode.HALF_UP);

        return new CustoMedioMilheiroResult(query.clienteId(), custoMedio);
    }

    // --- Convenience Methods ---

    @Transactional
    public Milhas registrarMilhas(UUID clienteId, Milhas milhas) {
        Objects.requireNonNull(clienteId, "clienteId eh obrigatorio");
        Objects.requireNonNull(milhas, "milhas eh obrigatorio");

        return milhasRepository.salvar(currentTenantId(), clienteId, milhas);
    }

    @Transactional(readOnly = true)
    public Optional<Milhas> buscarMilhas(UUID milhasId) {
        Objects.requireNonNull(milhasId, "milhasId eh obrigatorio");

        return milhasRepository.buscarPorId(currentTenantId(), milhasId);
    }

    @Transactional(readOnly = true)
    public List<Milhas> buscarMilhasPorCliente(UUID clienteId) {
        Objects.requireNonNull(clienteId, "clienteId eh obrigatorio");

        return milhasRepository.buscarPorCliente(currentTenantId(), clienteId);
    }

    @Transactional(readOnly = true)
    public List<Milhas> buscarMilhasPorPrograma(TipoProgramaMilhas programa) {
        Objects.requireNonNull(programa, "programa eh obrigatorio");

        return milhasRepository.buscarPorPrograma(currentTenantId(), programa);
    }

    @Transactional
    public Milhas atualizarMilhas(Milhas milhas) {
        Objects.requireNonNull(milhas, "milhas eh obrigatorio");
        Objects.requireNonNull(milhas.id(), "milhas.id eh obrigatorio");

        UUID tenantId = currentTenantId();
        if (!milhasRepository.existePorId(tenantId, milhas.id())) {
            throw new MilhasNaoEncontradaException(milhas.id());
        }

        return milhasRepository.atualizar(tenantId, milhas);
    }

    @Transactional
    public void removerMilhas(UUID milhasId) {
        Objects.requireNonNull(milhasId, "milhasId eh obrigatorio");

        execute(new RemoverMilhasCommand(milhasId));
    }

    @Transactional(readOnly = true)
    public int calcularSaldoTotal(UUID clienteId) {
        Objects.requireNonNull(clienteId, "clienteId eh obrigatorio");

        return calcularSaldoTotal(new ConsultarMilhasPorClienteQuery(clienteId)).saldoTotal();
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularCustoMedioMilheiro(UUID clienteId) {
        Objects.requireNonNull(clienteId, "clienteId eh obrigatorio");

        return calcularCustoMedioMilheiro(new ConsultarMilhasPorClienteQuery(clienteId)).custoMedioMilheiro();
    }
}
