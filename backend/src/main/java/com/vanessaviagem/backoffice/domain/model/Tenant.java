package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.StatusTenant;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa um tenant (inquilino) no sistema multi-tenant.
 * Cada tenant possui isolamento total de dados e chaves de criptografia próprias.
 */
public record Tenant(
        UUID tenantId,
        String nome,
        String cnpj,
        StatusTenant status,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
    public Tenant {
        Objects.requireNonNull(tenantId, "tenantId é obrigatório");
        Objects.requireNonNull(nome, "nome é obrigatório");
        Objects.requireNonNull(status, "status é obrigatório");
        Objects.requireNonNull(criadoEm, "criadoEm é obrigatório");

        if (nome.isBlank()) {
            throw new IllegalArgumentException("nome não pode ser vazio");
        }
    }

    /**
     * Cria um novo tenant com status ATIVO.
     */
    public static Tenant criar(String nome, String cnpj) {
        LocalDateTime agora = LocalDateTime.now();
        return new Tenant(
                UUID.randomUUID(),
                nome,
                cnpj,
                StatusTenant.ATIVO,
                agora,
                agora
        );
    }

    /**
     * Verifica se o tenant está ativo.
     */
    public boolean isAtivo() {
        return status == StatusTenant.ATIVO;
    }

    /**
     * Suspende o tenant temporariamente.
     */
    public Tenant suspender() {
        if (status == StatusTenant.BLOQUEADO) {
            throw new IllegalStateException("Tenant bloqueado não pode ser suspenso");
        }
        return new Tenant(tenantId, nome, cnpj, StatusTenant.SUSPENSO, criadoEm, LocalDateTime.now());
    }

    /**
     * Bloqueia o tenant permanentemente.
     */
    public Tenant bloquear() {
        return new Tenant(tenantId, nome, cnpj, StatusTenant.BLOQUEADO, criadoEm, LocalDateTime.now());
    }

    /**
     * Reativa o tenant.
     */
    public Tenant ativar() {
        if (status == StatusTenant.BLOQUEADO) {
            throw new IllegalStateException("Tenant bloqueado não pode ser reativado");
        }
        return new Tenant(tenantId, nome, cnpj, StatusTenant.ATIVO, criadoEm, LocalDateTime.now());
    }
}
