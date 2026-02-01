package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.TenantTestScenarios.CriarTenantScenario;
import com.vanessaviagem.backoffice.domain.model.TenantTestScenarios.TransicaoStatusScenario;
import com.vanessaviagem.backoffice.domain.model.TenantTestScenarios.ValidacaoScenario;
import com.vanessaviagem.backoffice.domain.model.enums.StatusTenant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Tenant")
class TenantTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.TenantTestDataProvider#criarTenantScenarios")
    @DisplayName("criar")
    void shouldCreateTenant(CriarTenantScenario scenario) {
        LocalDateTime antes = LocalDateTime.now();

        Tenant tenant = Tenant.criar(scenario.nome(), scenario.cnpj());

        LocalDateTime depois = LocalDateTime.now();

        assertNotNull(tenant.tenantId());
        assertEquals(scenario.nome(), tenant.nome());
        assertEquals(scenario.cnpj(), tenant.cnpj());
        assertEquals(StatusTenant.ATIVO, tenant.status());
        assertTrue(tenant.isAtivo());
        assertNotNull(tenant.criadoEm());
        assertNotNull(tenant.atualizadoEm());
        assertTrue(tenant.criadoEm().isAfter(antes.minusSeconds(1)));
        assertTrue(tenant.criadoEm().isBefore(depois.plusSeconds(1)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.TenantTestDataProvider#transicaoStatusScenarios")
    @DisplayName("transicao de status")
    void shouldHandleStatusTransition(TransicaoStatusScenario scenario) {
        Tenant tenant = criarTenantComStatus(scenario.statusInicial());

        if (scenario.deveSucceder()) {
            Tenant resultado = executarOperacao(tenant, scenario.operacao());
            assertEquals(scenario.statusEsperado(), resultado.status());
        } else {
            assertThrows(IllegalStateException.class, () ->
                    executarOperacao(tenant, scenario.operacao())
            );
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.TenantTestDataProvider#validacaoScenarios")
    @DisplayName("validacao")
    void shouldValidateInput(ValidacaoScenario scenario) {
        Exception exception = assertThrows(
                scenario.excecaoEsperada(),
                () -> Tenant.criar(scenario.nome(), scenario.cnpj())
        );

        assertTrue(exception.getMessage().contains(scenario.mensagemEsperada()));
    }

    private Tenant criarTenantComStatus(StatusTenant status) {
        Tenant tenant = Tenant.criar("Empresa Teste", "12.345.678/0001-90");
        return switch (status) {
            case ATIVO -> tenant;
            case SUSPENSO -> tenant.suspender();
            case BLOQUEADO -> tenant.bloquear();
        };
    }

    private Tenant executarOperacao(Tenant tenant, String operacao) {
        return switch (operacao) {
            case "suspender" -> tenant.suspender();
            case "bloquear" -> tenant.bloquear();
            case "ativar" -> tenant.ativar();
            default -> throw new IllegalArgumentException("Operação desconhecida: " + operacao);
        };
    }
}
