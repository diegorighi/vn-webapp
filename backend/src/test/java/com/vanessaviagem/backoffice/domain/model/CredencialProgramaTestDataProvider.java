package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.CredencialProgramaTestScenarios.CriarComOAuthScenario;
import com.vanessaviagem.backoffice.domain.model.CredencialProgramaTestScenarios.CriarComSenhaScenario;
import com.vanessaviagem.backoffice.domain.model.CredencialProgramaTestScenarios.TokenExpiradoScenario;
import com.vanessaviagem.backoffice.domain.model.CredencialProgramaTestScenarios.TransicaoStatusScenario;
import com.vanessaviagem.backoffice.domain.model.enums.StatusCredencial;
import com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas;
import org.junit.jupiter.params.provider.Arguments;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Provider de dados de teste para CredencialPrograma.
 */
public final class CredencialProgramaTestDataProvider {

    private static final byte[] ENCRYPTED_USER = "encrypted_user".getBytes();
    private static final byte[] ENCRYPTED_PASS = "encrypted_pass".getBytes();
    private static final byte[] ENCRYPTED_TOKEN = "encrypted_token".getBytes();
    private static final byte[] ENCRYPTED_REFRESH = "encrypted_refresh".getBytes();

    private CredencialProgramaTestDataProvider() {
    }

    @SuppressWarnings("unused")
    public static Stream<Arguments> criarComSenhaScenarios() {
        return Stream.of(
                Arguments.of(new CriarComSenhaScenario(
                        "should create Smiles credential with password",
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        TipoProgramaMilhas.SMILES,
                        ENCRYPTED_USER,
                        ENCRYPTED_PASS
                )),
                Arguments.of(new CriarComSenhaScenario(
                        "should create LATAM Pass credential with password",
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        TipoProgramaMilhas.LATAM_PASS,
                        ENCRYPTED_USER,
                        ENCRYPTED_PASS
                )),
                Arguments.of(new CriarComSenhaScenario(
                        "should create Azul Fidelidade credential with password",
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        TipoProgramaMilhas.AZUL_FIDELIDADE,
                        ENCRYPTED_USER,
                        ENCRYPTED_PASS
                ))
        );
    }

    @SuppressWarnings("unused")
    public static Stream<Arguments> criarComOAuthScenarios() {
        return Stream.of(
                Arguments.of(new CriarComOAuthScenario(
                        "should create Livelo credential with OAuth",
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        TipoProgramaMilhas.LIVELO,
                        ENCRYPTED_TOKEN,
                        ENCRYPTED_REFRESH,
                        LocalDateTime.now().plusHours(1)
                )),
                Arguments.of(new CriarComOAuthScenario(
                        "should create Esfera credential with OAuth",
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        TipoProgramaMilhas.ESFERA,
                        ENCRYPTED_TOKEN,
                        ENCRYPTED_REFRESH,
                        LocalDateTime.now().plusDays(30)
                ))
        );
    }

    @SuppressWarnings("unused")
    public static Stream<Arguments> transicaoStatusScenarios() {
        return Stream.of(
                Arguments.of(new TransicaoStatusScenario(
                        "ATIVA -> ERRO_AUTENTICACAO",
                        StatusCredencial.ATIVA,
                        "marcarErroAutenticacao",
                        StatusCredencial.ERRO_AUTENTICACAO
                )),
                Arguments.of(new TransicaoStatusScenario(
                        "ATIVA -> DESATIVADA",
                        StatusCredencial.ATIVA,
                        "desativar",
                        StatusCredencial.DESATIVADA
                )),
                Arguments.of(new TransicaoStatusScenario(
                        "ATIVA -> BLOQUEADA",
                        StatusCredencial.ATIVA,
                        "bloquear",
                        StatusCredencial.BLOQUEADA
                ))
        );
    }

    @SuppressWarnings("unused")
    public static Stream<Arguments> tokenExpiradoScenarios() {
        return Stream.of(
                Arguments.of(new TokenExpiradoScenario(
                        "should return true when token expired yesterday",
                        LocalDateTime.now().minusDays(1),
                        true
                )),
                Arguments.of(new TokenExpiradoScenario(
                        "should return true when token expired 1 minute ago",
                        LocalDateTime.now().minusMinutes(1),
                        true
                )),
                Arguments.of(new TokenExpiradoScenario(
                        "should return false when token expires in 1 hour",
                        LocalDateTime.now().plusHours(1),
                        false
                )),
                Arguments.of(new TokenExpiradoScenario(
                        "should return false when token expires tomorrow",
                        LocalDateTime.now().plusDays(1),
                        false
                ))
        );
    }
}
