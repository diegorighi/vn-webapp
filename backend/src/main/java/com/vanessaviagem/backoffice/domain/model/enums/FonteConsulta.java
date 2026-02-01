package com.vanessaviagem.backoffice.domain.model.enums;

/**
 * Fonte da consulta de saldo de milhas.
 */
public enum FonteConsulta {
    /**
     * API oficial do programa de milhas (OAuth, parceria formal).
     */
    API_OFICIAL,

    /**
     * Cliente HTTP com engenharia reversa (API mobile/web interceptada).
     */
    HTTP_CLIENT,

    /**
     * Automação de browser (Playwright/Selenium headless).
     */
    BROWSER_AUTOMATION,

    /**
     * Cache local - dado obtido anteriormente.
     */
    CACHE
}
