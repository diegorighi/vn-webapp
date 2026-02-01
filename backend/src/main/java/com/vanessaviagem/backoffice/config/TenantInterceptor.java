package com.vanessaviagem.backoffice.config;

import com.vanessaviagem.backoffice.application.ports.out.TenantRepository;
import com.vanessaviagem.backoffice.domain.model.TenantContext;
import com.vanessaviagem.backoffice.domain.model.enums.RoleType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Interceptor para extrair e validar o contexto do tenant a partir do JWT.
 *
 * <p>Suporta tanto Keycloak quanto AWS Cognito como Authorization Server.</p>
 *
 * <p>Diferenca de claims entre providers:</p>
 * <pre>
 * ┌────────────────┬─────────────────────────────┬─────────────────────────────┐
 * │ Claim          │ Keycloak                    │ Cognito                     │
 * ├────────────────┼─────────────────────────────┼─────────────────────────────┤
 * │ tenant_id      │ tenant_id                   │ custom:tenant_id            │
 * │ user_id        │ user_id ou sub              │ custom:user_id ou sub       │
 * │ email          │ email                       │ email                       │
 * │ roles          │ roles ou realm_access.roles │ cognito:groups              │
 * │ permissions    │ permissions                 │ custom:permissions          │
 * │ can_approve    │ can_approve                 │ custom:can_approve          │
 * └────────────────┴─────────────────────────────┴─────────────────────────────┘
 * </pre>
 */
@Component
@Profile("!dev")
public class TenantInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(TenantInterceptor.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtDecoder jwtDecoder;
    private final TenantRepository tenantRepository;

    public TenantInterceptor(JwtDecoder jwtDecoder, TenantRepository tenantRepository) {
        this.jwtDecoder = jwtDecoder;
        this.tenantRepository = tenantRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();

        if (isPublicPath(path)) {
            return true;
        }

        String token = extractToken(request);
        if (token == null) {
            log.warn("Requisicao sem token JWT: {} {}", request.getMethod(), path);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        try {
            Jwt jwt = jwtDecoder.decode(token);

            UUID tenantId = extractTenantId(jwt);
            UUID userId = extractUserId(jwt);
            String userEmail = extractEmail(jwt);
            Set<String> permissions = extractPermissions(jwt);
            RoleType highestRole = extractHighestRole(jwt);
            boolean canApprove = extractCanApprove(jwt);

            if (!tenantRepository.existeEAtivo(tenantId)) {
                log.warn("Tenant nao encontrado ou inativo: {}", tenantId);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }

            TenantContext.set(tenantId, userId, userEmail, permissions, highestRole, canApprove);

            log.debug("TenantContext configurado: tenant={}, user={}, email={}, role={}",
                    tenantId, userId, userEmail, highestRole);
            return true;

        } catch (Exception e) {
            log.error("Erro ao processar token JWT: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex
    ) {
        TenantContext.clear();
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * Extrai tenant_id (Keycloak: tenant_id, Cognito: custom:tenant_id)
     */
    private UUID extractTenantId(Jwt jwt) {
        String tenantId = jwt.getClaimAsString("tenant_id");
        if (tenantId == null) {
            tenantId = jwt.getClaimAsString("custom:tenant_id");
        }
        if (tenantId == null) {
            throw new SecurityException("Claim 'tenant_id' nao encontrado no token");
        }
        return UUID.fromString(tenantId);
    }

    /**
     * Extrai user_id (Keycloak: user_id ou sub, Cognito: custom:user_id ou sub)
     */
    private UUID extractUserId(Jwt jwt) {
        String userId = jwt.getClaimAsString("user_id");
        if (userId == null) {
            userId = jwt.getClaimAsString("custom:user_id");
        }
        if (userId == null) {
            userId = jwt.getSubject();
        }
        if (userId == null) {
            throw new SecurityException("Claim 'user_id' ou 'sub' nao encontrado no token");
        }
        return UUID.fromString(userId);
    }

    /**
     * Extrai email do usuario
     */
    private String extractEmail(Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        if (email == null) {
            email = jwt.getClaimAsString("username");
        }
        return email != null ? email : "unknown";
    }

    /**
     * Extrai permissions (Keycloak: permissions, Cognito: custom:permissions ou scope)
     */
    @SuppressWarnings("unchecked")
    private Set<String> extractPermissions(Jwt jwt) {
        // Try Keycloak format
        Object permissions = jwt.getClaim("permissions");
        if (permissions instanceof List) {
            return new HashSet<>((List<String>) permissions);
        }
        if (permissions instanceof String) {
            return Set.of(((String) permissions).split(","));
        }

        // Try Cognito format
        String customPermissions = jwt.getClaimAsString("custom:permissions");
        if (customPermissions != null) {
            return Set.of(customPermissions.split(","));
        }

        // Derive from roles if no explicit permissions
        Set<String> derivedPermissions = new HashSet<>();
        List<String> roles = extractRoles(jwt);
        for (String role : roles) {
            derivedPermissions.addAll(getDefaultPermissionsForRole(role));
        }
        return derivedPermissions;
    }

    /**
     * Extrai roles do JWT (Keycloak ou Cognito)
     */
    @SuppressWarnings("unchecked")
    private List<String> extractRoles(Jwt jwt) {
        // Try direct roles claim (Keycloak custom mapper)
        List<String> roles = jwt.getClaim("roles");
        if (roles != null && !roles.isEmpty()) {
            return roles;
        }

        // Try Keycloak realm_access.roles
        Object realmAccess = jwt.getClaim("realm_access");
        if (realmAccess instanceof java.util.Map) {
            Object realmRoles = ((java.util.Map<String, Object>) realmAccess).get("roles");
            if (realmRoles instanceof List) {
                return (List<String>) realmRoles;
            }
        }

        // Try Cognito groups
        List<String> cognitoGroups = jwt.getClaim("cognito:groups");
        if (cognitoGroups != null && !cognitoGroups.isEmpty()) {
            return cognitoGroups;
        }

        // Try custom:role (single role)
        String customRole = jwt.getClaimAsString("custom:role");
        if (customRole != null) {
            return List.of(customRole);
        }

        return List.of("VIEWER"); // Default role
    }

    /**
     * Determina o role de maior privilegio do usuario
     */
    private RoleType extractHighestRole(Jwt jwt) {
        List<String> roles = extractRoles(jwt);

        // Order of precedence (highest first)
        String[] orderedRoles = {"ROOT", "ADMIN", "MANAGER", "OPERATOR", "VIEWER"};

        for (String orderedRole : orderedRoles) {
            if (roles.stream().anyMatch(r -> r.equalsIgnoreCase(orderedRole))) {
                return RoleType.valueOf(orderedRole);
            }
        }

        return RoleType.VIEWER; // Default
    }

    /**
     * Extrai can_approve flag
     */
    private boolean extractCanApprove(Jwt jwt) {
        // Try direct claim
        Object canApprove = jwt.getClaim("can_approve");
        if (canApprove instanceof Boolean) {
            return (Boolean) canApprove;
        }
        if (canApprove instanceof String) {
            return Boolean.parseBoolean((String) canApprove);
        }

        // Try Cognito custom attribute
        String customCanApprove = jwt.getClaimAsString("custom:can_approve");
        if (customCanApprove != null) {
            return Boolean.parseBoolean(customCanApprove);
        }

        // Derive from role
        RoleType role = extractHighestRole(jwt);
        return role == RoleType.ROOT || role == RoleType.ADMIN;
    }

    /**
     * Retorna permissoes padrao para um role
     */
    private Set<String> getDefaultPermissionsForRole(String role) {
        return switch (role.toUpperCase()) {
            case "ROOT" -> Set.of("*");
            case "ADMIN" -> Set.of("users:read", "users:write", "data:read", "data:write", "data:delete", "approvals:manage");
            case "MANAGER" -> Set.of("data:read", "data:write:pending", "reports:read");
            case "OPERATOR" -> Set.of("data:read", "data:insert:pending");
            case "VIEWER" -> Set.of("data:read");
            default -> Set.of("data:read");
        };
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/actuator/health") ||
                path.startsWith("/public/") ||
                path.equals("/") ||
                path.startsWith("/swagger") ||
                path.startsWith("/v3/api-docs");
    }
}
