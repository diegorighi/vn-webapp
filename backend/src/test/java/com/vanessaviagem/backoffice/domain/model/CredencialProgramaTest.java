package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.CredencialProgramaTestScenarios.CriarComOAuthScenario;
import com.vanessaviagem.backoffice.domain.model.CredencialProgramaTestScenarios.CriarComSenhaScenario;
import com.vanessaviagem.backoffice.domain.model.CredencialProgramaTestScenarios.TokenExpiradoScenario;
import com.vanessaviagem.backoffice.domain.model.CredencialProgramaTestScenarios.TransicaoStatusScenario;
import com.vanessaviagem.backoffice.domain.model.enums.StatusCredencial;
import com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("CredencialPrograma")
class CredencialProgramaTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.CredencialProgramaTestDataProvider#criarComSenhaScenarios")
    @DisplayName("criarComSenha")
    void shouldCreateCredentialWithPassword(CriarComSenhaScenario scenario) {
        CredencialPrograma credencial = CredencialPrograma.criarComSenha(
                scenario.tenantId(),
                scenario.clienteId(),
                scenario.programa(),
                scenario.usuarioCriptografado(),
                scenario.senhaCriptografada()
        );

        assertNotNull(credencial.id());
        assertEquals(scenario.tenantId(), credencial.tenantId());
        assertEquals(scenario.clienteId(), credencial.clienteId());
        assertEquals(scenario.programa(), credencial.programa());
        assertNotNull(credencial.usuarioCriptografado());
        assertNotNull(credencial.senhaCriptografada());
        assertNull(credencial.accessTokenCriptografado());
        assertNull(credencial.refreshTokenCriptografado());
        assertEquals(StatusCredencial.ATIVA, credencial.status());
        assertTrue(credencial.isAtiva());
        assertFalse(credencial.isOAuth());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.CredencialProgramaTestDataProvider#criarComOAuthScenarios")
    @DisplayName("criarComOAuth")
    void shouldCreateCredentialWithOAuth(CriarComOAuthScenario scenario) {
        CredencialPrograma credencial = CredencialPrograma.criarComOAuth(
                scenario.tenantId(),
                scenario.clienteId(),
                scenario.programa(),
                scenario.accessToken(),
                scenario.refreshToken(),
                scenario.tokenExpiraEm()
        );

        assertNotNull(credencial.id());
        assertEquals(scenario.tenantId(), credencial.tenantId());
        assertEquals(scenario.clienteId(), credencial.clienteId());
        assertEquals(scenario.programa(), credencial.programa());
        assertNull(credencial.usuarioCriptografado());
        assertNull(credencial.senhaCriptografada());
        assertNotNull(credencial.accessTokenCriptografado());
        assertNotNull(credencial.refreshTokenCriptografado());
        assertEquals(scenario.tokenExpiraEm(), credencial.tokenExpiraEm());
        assertEquals(StatusCredencial.ATIVA, credencial.status());
        assertTrue(credencial.isAtiva());
        assertTrue(credencial.isOAuth());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.CredencialProgramaTestDataProvider#transicaoStatusScenarios")
    @DisplayName("transicao de status")
    void shouldTransitionStatus(TransicaoStatusScenario scenario) {
        CredencialPrograma credencial = criarCredencialAtiva();

        CredencialPrograma resultado = executarOperacao(credencial, scenario.operacao());

        assertEquals(scenario.statusEsperado(), resultado.status());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.CredencialProgramaTestDataProvider#tokenExpiradoScenarios")
    @DisplayName("isTokenExpirado")
    void shouldCheckTokenExpiration(TokenExpiradoScenario scenario) {
        CredencialPrograma credencial = CredencialPrograma.criarComOAuth(
                UUID.randomUUID(),
                UUID.randomUUID(),
                TipoProgramaMilhas.LIVELO,
                "token".getBytes(),
                "refresh".getBytes(),
                scenario.tokenExpiraEm()
        );

        assertEquals(scenario.expectedExpirado(), credencial.isTokenExpirado());
    }

    @ParameterizedTest(name = "registrarConsulta should update ultima_consulta - {0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.CredencialProgramaTestDataProvider#criarComSenhaScenarios")
    @DisplayName("registrarConsulta")
    void shouldRegisterQuery(CriarComSenhaScenario scenario) {
        CredencialPrograma credencial = CredencialPrograma.criarComSenha(
                scenario.tenantId(),
                scenario.clienteId(),
                scenario.programa(),
                scenario.usuarioCriptografado(),
                scenario.senhaCriptografada()
        );

        assertNull(credencial.ultimaConsulta());

        LocalDateTime antes = LocalDateTime.now();
        CredencialPrograma atualizada = credencial.registrarConsulta();
        LocalDateTime depois = LocalDateTime.now();

        assertNotNull(atualizada.ultimaConsulta());
        assertTrue(atualizada.ultimaConsulta().isAfter(antes.minusSeconds(1)));
        assertTrue(atualizada.ultimaConsulta().isBefore(depois.plusSeconds(1)));
    }

    @ParameterizedTest(name = "atualizarTokens should update OAuth tokens - {0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.CredencialProgramaTestDataProvider#criarComOAuthScenarios")
    @DisplayName("atualizarTokens")
    void shouldUpdateTokens(CriarComOAuthScenario scenario) {
        CredencialPrograma credencial = CredencialPrograma.criarComOAuth(
                scenario.tenantId(),
                scenario.clienteId(),
                scenario.programa(),
                scenario.accessToken(),
                scenario.refreshToken(),
                scenario.tokenExpiraEm()
        );

        byte[] novoAccessToken = "new_access_token".getBytes();
        byte[] novoRefreshToken = "new_refresh_token".getBytes();
        LocalDateTime novaExpiracao = LocalDateTime.now().plusHours(2);

        CredencialPrograma atualizada = credencial.atualizarTokens(
                novoAccessToken, novoRefreshToken, novaExpiracao
        );

        assertEquals(novoAccessToken, atualizada.accessTokenCriptografado());
        assertEquals(novoRefreshToken, atualizada.refreshTokenCriptografado());
        assertEquals(novaExpiracao, atualizada.tokenExpiraEm());
        assertEquals(StatusCredencial.ATIVA, atualizada.status());
    }

    private CredencialPrograma criarCredencialAtiva() {
        return CredencialPrograma.criarComSenha(
                UUID.randomUUID(),
                UUID.randomUUID(),
                TipoProgramaMilhas.SMILES,
                "user".getBytes(),
                "pass".getBytes()
        );
    }

    private CredencialPrograma executarOperacao(CredencialPrograma credencial, String operacao) {
        return switch (operacao) {
            case "marcarErroAutenticacao" -> credencial.marcarErroAutenticacao();
            case "desativar" -> credencial.desativar();
            case "bloquear" -> credencial.bloquear();
            default -> throw new IllegalArgumentException("Operação desconhecida: " + operacao);
        };
    }
}
