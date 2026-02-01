package com.vanessaviagem.backoffice.adapters.in.graphql;

import com.vanessaviagem.backoffice.application.services.SaldoMilhasService;
import com.vanessaviagem.backoffice.domain.model.SaldoMilhas;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class SaldoMilhasResolver {

    private final SaldoMilhasService service;

    public SaldoMilhasResolver(SaldoMilhasService service) {
        this.service = service;
    }

    // ==================== QUERIES ====================

    @QueryMapping
    public List<SaldoMilhas> saldosMilhas() {
        return service.listarTodos();
    }

    @QueryMapping
    public List<SaldoMilhas> saldosMilhasPorOwner(@Argument String owner) {
        return service.listarPorOwner(owner);
    }

    @QueryMapping
    public long totalMilhas() {
        return service.totalMilhas();
    }

    @QueryMapping
    public List<TotalPorOwner> totaisPorOwner() {
        return service.totaisPorOwner().entrySet().stream()
                .map(e -> new TotalPorOwner(e.getKey(), e.getValue()))
                .toList();
    }

    @QueryMapping
    public List<TotalPorPrograma> totaisPorPrograma() {
        return service.totaisPorPrograma().entrySet().stream()
                .map(e -> new TotalPorPrograma(e.getKey(), e.getValue()))
                .toList();
    }

    @QueryMapping
    public ResumoMilhas resumoMilhas() {
        List<SaldoMilhas> saldos = service.listarTodos();
        long totalGeral = service.totalMilhas();
        Map<String, Long> porOwner = service.totaisPorOwner();
        Map<String, Long> porPrograma = service.totaisPorPrograma();

        return new ResumoMilhas(
                totalGeral,
                saldos.size(),
                porOwner.entrySet().stream()
                        .map(e -> new TotalPorOwner(e.getKey(), e.getValue()))
                        .toList(),
                porPrograma.entrySet().stream()
                        .map(e -> new TotalPorPrograma(e.getKey(), e.getValue()))
                        .toList(),
                saldos
        );
    }

    // ==================== MUTATIONS ====================

    @MutationMapping
    public SaldoMilhas registrarSaldo(@Argument SaldoMilhasInput input) {
        return service.registrarSaldo(input.programa(), input.owner(), input.quantidade());
    }

    @MutationMapping
    public boolean removerSaldo(@Argument String id) {
        service.remover(UUID.fromString(id));
        return true;
    }

    // ==================== DTOs ====================

    public record SaldoMilhasInput(String programa, String owner, long quantidade) {}

    public record TotalPorOwner(String owner, long total) {}

    public record TotalPorPrograma(String programa, long total) {}

    public record ResumoMilhas(
            long totalGeral,
            int quantidadeRegistros,
            List<TotalPorOwner> porOwner,
            List<TotalPorPrograma> porPrograma,
            List<SaldoMilhas> saldos
    ) {}
}
