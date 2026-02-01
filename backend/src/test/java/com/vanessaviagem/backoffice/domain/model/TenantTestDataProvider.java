package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.TenantTestScenarios.CriarTenantScenario;
import com.vanessaviagem.backoffice.domain.model.TenantTestScenarios.TransicaoStatusScenario;
import com.vanessaviagem.backoffice.domain.model.TenantTestScenarios.ValidacaoScenario;
import com.vanessaviagem.backoffice.domain.model.enums.StatusTenant;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

/**
 * Provider de dados de teste para Tenant.
 */
public final class TenantTestDataProvider {

    private TenantTestDataProvider() {
    }

    @SuppressWarnings("unused")
    public static Stream<Arguments> criarTenantScenarios() {
        return Stream.of(
                Arguments.of(new CriarTenantScenario(
                        "should create tenant with nome and cnpj",
                        "Empresa ABC",
                        "12.345.678/0001-90"
                )),
                Arguments.of(new CriarTenantScenario(
                        "should create tenant with nome only",
                        "Empresa XYZ",
                        null
                )),
                Arguments.of(new CriarTenantScenario(
                        "should create tenant with long name",
                        "Empresa com Nome Muito Grande para Testar Limites do Sistema",
                        "98.765.432/0001-10"
                ))
        );
    }

    @SuppressWarnings("unused")
    public static Stream<Arguments> transicaoStatusScenarios() {
        return Stream.of(
                Arguments.of(new TransicaoStatusScenario(
                        "ATIVO -> SUSPENSO: should succeed",
                        StatusTenant.ATIVO,
                        "suspender",
                        StatusTenant.SUSPENSO,
                        true
                )),
                Arguments.of(new TransicaoStatusScenario(
                        "ATIVO -> BLOQUEADO: should succeed",
                        StatusTenant.ATIVO,
                        "bloquear",
                        StatusTenant.BLOQUEADO,
                        true
                )),
                Arguments.of(new TransicaoStatusScenario(
                        "SUSPENSO -> ATIVO: should succeed",
                        StatusTenant.SUSPENSO,
                        "ativar",
                        StatusTenant.ATIVO,
                        true
                )),
                Arguments.of(new TransicaoStatusScenario(
                        "SUSPENSO -> BLOQUEADO: should succeed",
                        StatusTenant.SUSPENSO,
                        "bloquear",
                        StatusTenant.BLOQUEADO,
                        true
                )),
                Arguments.of(new TransicaoStatusScenario(
                        "BLOQUEADO -> SUSPENSO: should fail",
                        StatusTenant.BLOQUEADO,
                        "suspender",
                        null,
                        false
                )),
                Arguments.of(new TransicaoStatusScenario(
                        "BLOQUEADO -> ATIVO: should fail",
                        StatusTenant.BLOQUEADO,
                        "ativar",
                        null,
                        false
                ))
        );
    }

    @SuppressWarnings("unused")
    public static Stream<Arguments> validacaoScenarios() {
        return Stream.of(
                Arguments.of(new ValidacaoScenario(
                        "should reject null nome",
                        null,
                        "12.345.678/0001-90",
                        NullPointerException.class,
                        "nome é obrigatório"
                )),
                Arguments.of(new ValidacaoScenario(
                        "should reject blank nome",
                        "   ",
                        "12.345.678/0001-90",
                        IllegalArgumentException.class,
                        "nome não pode ser vazio"
                )),
                Arguments.of(new ValidacaoScenario(
                        "should reject empty nome",
                        "",
                        "12.345.678/0001-90",
                        IllegalArgumentException.class,
                        "nome não pode ser vazio"
                ))
        );
    }
}
