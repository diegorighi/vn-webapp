package com.vanessaviagem.backoffice.domain.model.enums;

/**
 * Status da credencial de acesso a um programa de milhas.
 */
public enum StatusCredencial {
    /**
     * Credencial ativa e funcionando.
     */
    ATIVA,

    /**
     * Credencial com erro de autenticação - necessita atualização.
     */
    ERRO_AUTENTICACAO,

    /**
     * Credencial expirada - token OAuth expirou.
     */
    EXPIRADA,

    /**
     * Credencial desativada pelo usuário.
     */
    DESATIVADA,

    /**
     * Credencial bloqueada por segurança.
     */
    BLOQUEADA
}
