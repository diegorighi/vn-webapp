package com.vanessaviagem.backoffice.application.services;

import com.vanessaviagem.backoffice.application.ports.out.SaldoMilhasRepository;
import com.vanessaviagem.backoffice.domain.model.SaldoMilhas;
import com.vanessaviagem.backoffice.domain.model.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SaldoMilhasService {

    private final SaldoMilhasRepository repository;

    public SaldoMilhasService(SaldoMilhasRepository repository) {
        this.repository = repository;
    }

    /**
     * Registra ou atualiza um saldo de milhas.
     * Se já existe registro para programa+owner, atualiza. Senão, cria novo.
     */
    @Transactional
    public SaldoMilhas registrarSaldo(String programa, String owner, long quantidade) {
        UUID tenantId = TenantContext.current().tenantId();

        Optional<SaldoMilhas> existente = repository.buscarPorProgramaEOwner(tenantId, programa, owner);

        if (existente.isPresent()) {
            SaldoMilhas atualizado = existente.get().atualizarQuantidade(quantidade);
            return repository.atualizar(atualizado);
        } else {
            SaldoMilhas novo = SaldoMilhas.criar(tenantId, programa, owner, quantidade);
            return repository.salvar(novo);
        }
    }

    /**
     * Lista todos os saldos do tenant.
     */
    @Transactional(readOnly = true)
    public List<SaldoMilhas> listarTodos() {
        UUID tenantId = TenantContext.current().tenantId();
        return repository.listarPorTenant(tenantId);
    }

    /**
     * Lista saldos de um owner específico.
     */
    @Transactional(readOnly = true)
    public List<SaldoMilhas> listarPorOwner(String owner) {
        UUID tenantId = TenantContext.current().tenantId();
        return repository.listarPorOwner(tenantId, owner);
    }

    /**
     * Retorna o total de milhas do tenant.
     */
    @Transactional(readOnly = true)
    public long totalMilhas() {
        UUID tenantId = TenantContext.current().tenantId();
        return repository.totalMilhasPorTenant(tenantId);
    }

    /**
     * Retorna totais agrupados por owner.
     */
    @Transactional(readOnly = true)
    public Map<String, Long> totaisPorOwner() {
        UUID tenantId = TenantContext.current().tenantId();
        List<SaldoMilhas> todos = repository.listarPorTenant(tenantId);

        return todos.stream()
                .collect(Collectors.groupingBy(
                        SaldoMilhas::owner,
                        Collectors.summingLong(SaldoMilhas::quantidade)
                ));
    }

    /**
     * Retorna totais agrupados por programa.
     */
    @Transactional(readOnly = true)
    public Map<String, Long> totaisPorPrograma() {
        UUID tenantId = TenantContext.current().tenantId();
        List<SaldoMilhas> todos = repository.listarPorTenant(tenantId);

        return todos.stream()
                .collect(Collectors.groupingBy(
                        SaldoMilhas::programa,
                        Collectors.summingLong(SaldoMilhas::quantidade)
                ));
    }

    /**
     * Remove um registro de saldo.
     */
    @Transactional
    public void remover(UUID id) {
        UUID tenantId = TenantContext.current().tenantId();
        repository.remover(tenantId, id);
    }
}
