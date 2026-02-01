package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.StatusCredencial;
import com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa uma credencial de acesso a um programa de milhas.
 * Os dados sensíveis (usuário, senha, tokens) são armazenados criptografados
 * usando a DEK (Data Encryption Key) específica do tenant.
 *
 * <p>IMPORTANTE: Esta entidade contém apenas os dados criptografados.
 * A descriptografia é responsabilidade do TenantCryptoService.</p>
 */
public record CredencialPrograma(
        UUID id,
        UUID tenantId,
        UUID clienteId,
        TipoProgramaMilhas programa,
        byte[] usuarioCriptografado,
        byte[] senhaCriptografada,
        byte[] accessTokenCriptografado,
        byte[] refreshTokenCriptografado,
        LocalDateTime tokenExpiraEm,
        LocalDateTime ultimaConsulta,
        StatusCredencial status,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
    public CredencialPrograma {
        Objects.requireNonNull(id, "id é obrigatório");
        Objects.requireNonNull(tenantId, "tenantId é obrigatório");
        Objects.requireNonNull(clienteId, "clienteId é obrigatório");
        Objects.requireNonNull(programa, "programa é obrigatório");
        Objects.requireNonNull(status, "status é obrigatório");
        Objects.requireNonNull(criadoEm, "criadoEm é obrigatório");

        if (usuarioCriptografado == null && accessTokenCriptografado == null) {
            throw new IllegalArgumentException("Deve fornecer usuário/senha ou tokens OAuth");
        }
    }

    /**
     * Cria uma nova credencial com usuário e senha.
     */
    public static CredencialPrograma criarComSenha(
            UUID tenantId,
            UUID clienteId,
            TipoProgramaMilhas programa,
            byte[] usuarioCriptografado,
            byte[] senhaCriptografada
    ) {
        LocalDateTime agora = LocalDateTime.now();
        return new CredencialPrograma(
                UUID.randomUUID(),
                tenantId,
                clienteId,
                programa,
                usuarioCriptografado,
                senhaCriptografada,
                null,
                null,
                null,
                null,
                StatusCredencial.ATIVA,
                agora,
                agora
        );
    }

    /**
     * Cria uma nova credencial com tokens OAuth.
     */
    public static CredencialPrograma criarComOAuth(
            UUID tenantId,
            UUID clienteId,
            TipoProgramaMilhas programa,
            byte[] accessTokenCriptografado,
            byte[] refreshTokenCriptografado,
            LocalDateTime tokenExpiraEm
    ) {
        LocalDateTime agora = LocalDateTime.now();
        return new CredencialPrograma(
                UUID.randomUUID(),
                tenantId,
                clienteId,
                programa,
                null,
                null,
                accessTokenCriptografado,
                refreshTokenCriptografado,
                tokenExpiraEm,
                null,
                StatusCredencial.ATIVA,
                agora,
                agora
        );
    }

    /**
     * Verifica se a credencial está ativa e pode ser usada.
     */
    public boolean isAtiva() {
        return status == StatusCredencial.ATIVA;
    }

    /**
     * Verifica se a credencial usa OAuth.
     */
    public boolean isOAuth() {
        return accessTokenCriptografado != null;
    }

    /**
     * Verifica se o token OAuth expirou.
     */
    public boolean isTokenExpirado() {
        if (!isOAuth() || tokenExpiraEm == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(tokenExpiraEm);
    }

    /**
     * Atualiza os tokens OAuth.
     */
    public CredencialPrograma atualizarTokens(
            byte[] novoAccessToken,
            byte[] novoRefreshToken,
            LocalDateTime novaExpiracao
    ) {
        return new CredencialPrograma(
                id, tenantId, clienteId, programa,
                usuarioCriptografado, senhaCriptografada,
                novoAccessToken, novoRefreshToken, novaExpiracao,
                ultimaConsulta, StatusCredencial.ATIVA,
                criadoEm, LocalDateTime.now()
        );
    }

    /**
     * Registra a última consulta realizada.
     */
    public CredencialPrograma registrarConsulta() {
        return new CredencialPrograma(
                id, tenantId, clienteId, programa,
                usuarioCriptografado, senhaCriptografada,
                accessTokenCriptografado, refreshTokenCriptografado, tokenExpiraEm,
                LocalDateTime.now(), status,
                criadoEm, LocalDateTime.now()
        );
    }

    /**
     * Marca a credencial com erro de autenticação.
     */
    public CredencialPrograma marcarErroAutenticacao() {
        return new CredencialPrograma(
                id, tenantId, clienteId, programa,
                usuarioCriptografado, senhaCriptografada,
                accessTokenCriptografado, refreshTokenCriptografado, tokenExpiraEm,
                ultimaConsulta, StatusCredencial.ERRO_AUTENTICACAO,
                criadoEm, LocalDateTime.now()
        );
    }

    /**
     * Desativa a credencial.
     */
    public CredencialPrograma desativar() {
        return new CredencialPrograma(
                id, tenantId, clienteId, programa,
                usuarioCriptografado, senhaCriptografada,
                accessTokenCriptografado, refreshTokenCriptografado, tokenExpiraEm,
                ultimaConsulta, StatusCredencial.DESATIVADA,
                criadoEm, LocalDateTime.now()
        );
    }

    /**
     * Bloqueia a credencial por segurança.
     */
    public CredencialPrograma bloquear() {
        return new CredencialPrograma(
                id, tenantId, clienteId, programa,
                usuarioCriptografado, senhaCriptografada,
                accessTokenCriptografado, refreshTokenCriptografado, tokenExpiraEm,
                ultimaConsulta, StatusCredencial.BLOQUEADA,
                criadoEm, LocalDateTime.now()
        );
    }
}
