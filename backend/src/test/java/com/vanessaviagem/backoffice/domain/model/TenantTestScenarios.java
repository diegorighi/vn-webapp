package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.StatusTenant;

/**
 * Cenários de teste para Tenant.
 */
public final class TenantTestScenarios {

    private TenantTestScenarios() {
    }

    public record CriarTenantScenario(
            String description,
            String nome,
            String cnpj
    ) {
        @Override
        public String toString() {
            return description;
        }
    }

    public record TransicaoStatusScenario(
            String description,
            StatusTenant statusInicial,
            String operacao,
            StatusTenant statusEsperado,
            boolean deveSucceder
    ) {
        @Override
        public String toString() {
            return description;
        }
    }

    public record ValidacaoScenario(
            String description,
            String nome,
            String cnpj,
            Class<? extends Exception> excecaoEsperada,
            String mensagemEsperada
    ) {
        @Override
        public String toString() {
            return description;
        }
    }
}
