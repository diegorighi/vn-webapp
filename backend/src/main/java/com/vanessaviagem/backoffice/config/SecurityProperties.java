package com.vanessaviagem.backoffice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propriedades de configuração de segurança.
 */
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    /**
     * URI do endpoint JWKS para validação de tokens JWT.
     */
    private String jwkSetUri = "http://localhost:8080/realms/vanessa/.well-known/jwks.json";

    /**
     * ID do cliente OAuth2.
     */
    private String clientId;

    /**
     * Secret do cliente OAuth2.
     */
    private String clientSecret;

    /**
     * Issuer esperado no token JWT.
     */
    private String issuer;

    public String getJwkSetUri() {
        return jwkSetUri;
    }

    public void setJwkSetUri(String jwkSetUri) {
        this.jwkSetUri = jwkSetUri;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}
