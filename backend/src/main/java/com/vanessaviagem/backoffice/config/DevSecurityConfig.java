package com.vanessaviagem.backoffice.config;

import com.vanessaviagem.backoffice.domain.model.TenantContext;
import com.vanessaviagem.backoffice.domain.model.enums.RoleType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Configuracao de seguranca para ambiente de desenvolvimento.
 *
 * <p>Opcoes:</p>
 * <ul>
 *   <li>security.disabled=true: Injeta TenantContext fixo (ADMIN) sem autenticacao</li>
 *   <li>security.disabled=false: Usa Keycloak local (docker-compose)</li>
 * </ul>
 *
 * <p>Para testar diferentes roles, altere o DEV_ROLE abaixo ou use Keycloak local.</p>
 */
@Configuration
@EnableWebSecurity
@Profile("dev")
public class DevSecurityConfig {

    // ==========================================================================
    // CONFIGURACAO DE TESTE - Altere para testar diferentes roles
    // ==========================================================================

    /**
     * Role para desenvolvimento. Altere para testar diferentes permissoes:
     * - ROOT: Controle total
     * - ADMIN: Pode aprovar, gerenciar usuarios
     * - MANAGER: Insert/Update com aprovacao
     * - OPERATOR: Apenas insert com aprovacao
     * - VIEWER: Somente leitura
     */
    private static final RoleType DEV_ROLE = RoleType.ADMIN;

    // IDs do seed data (V001__create_tables.sql)
    private static final UUID DEV_TENANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    // User IDs por role (do seed data)
    private static final UUID ROOT_USER_ID = UUID.fromString("20000000-0000-0000-0000-000000000001");
    private static final UUID ADMIN_USER_ID = UUID.fromString("20000000-0000-0000-0000-000000000002");
    private static final UUID MANAGER_USER_ID = UUID.fromString("20000000-0000-0000-0000-000000000003");
    private static final UUID OPERATOR_USER_ID = UUID.fromString("20000000-0000-0000-0000-000000000004");
    private static final UUID VIEWER_USER_ID = UUID.fromString("20000000-0000-0000-0000-000000000005");

    @Bean
    public SecurityFilterChain devSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)) // H2 Console
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/graphql/**").permitAll()
                        .requestMatchers("/graphiql/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(new DevTenantFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Filter que injeta TenantContext para desenvolvimento.
     * Suporta headers do DevToolbar do frontend para trocar tenant/role dinamicamente.
     */
    private static class DevTenantFilter extends OncePerRequestFilter {

        private static final String HEADER_TENANT_ID = "X-Tenant-Id";
        private static final String HEADER_USER_EMAIL = "X-User-Email";
        private static final String HEADER_USER_ROLES = "X-User-Roles";

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            try {
                // Ler tenant do header ou usar default
                UUID tenantId = parseTenantId(request.getHeader(HEADER_TENANT_ID));

                // Ler role do header ou usar default
                RoleType role = parseRole(request.getHeader(HEADER_USER_ROLES));

                UUID userId = getUserIdForRole(role);
                String email = request.getHeader(HEADER_USER_EMAIL) != null
                        ? request.getHeader(HEADER_USER_EMAIL)
                        : getEmailForRole(role);
                Set<String> permissions = getPermissionsForRole(role);
                boolean canApprove = role == RoleType.ROOT || role == RoleType.ADMIN;

                TenantContext.set(tenantId, userId, email, permissions, role, canApprove);

                filterChain.doFilter(request, response);
            } finally {
                TenantContext.clear();
            }
        }

        private UUID parseTenantId(String headerValue) {
            if (headerValue != null && !headerValue.isBlank()) {
                try {
                    return UUID.fromString(headerValue);
                } catch (IllegalArgumentException ignored) {
                    // Usar default se UUID invalido
                }
            }
            return DEV_TENANT_ID;
        }

        private RoleType parseRole(String headerValue) {
            if (headerValue != null && !headerValue.isBlank()) {
                // Pega o primeiro role se houver multiplos (ROOT,ADMIN -> ROOT)
                String firstRole = headerValue.split(",")[0].trim().toUpperCase();
                try {
                    return RoleType.valueOf(firstRole);
                } catch (IllegalArgumentException ignored) {
                    // Usar default se role invalido
                }
            }
            return DEV_ROLE;
        }

        private UUID getUserIdForRole(RoleType role) {
            return switch (role) {
                case ROOT -> ROOT_USER_ID;
                case ADMIN -> ADMIN_USER_ID;
                case MANAGER -> MANAGER_USER_ID;
                case OPERATOR -> OPERATOR_USER_ID;
                case VIEWER -> VIEWER_USER_ID;
            };
        }

        private String getEmailForRole(RoleType role) {
            return switch (role) {
                case ROOT -> "root@vanessaviagem.com.br";
                case ADMIN -> "admin@vanessaviagem.com.br";
                case MANAGER -> "manager@vanessaviagem.com.br";
                case OPERATOR -> "operator@vanessaviagem.com.br";
                case VIEWER -> "viewer@vanessaviagem.com.br";
            };
        }

        private Set<String> getPermissionsForRole(RoleType role) {
            return switch (role) {
                case ROOT -> Set.of("*");
                case ADMIN -> Set.of("users:read", "users:write", "data:read", "data:write", "data:delete", "approvals:manage");
                case MANAGER -> Set.of("data:read", "data:write:pending", "reports:read");
                case OPERATOR -> Set.of("data:read", "data:insert:pending");
                case VIEWER -> Set.of("data:read");
            };
        }
    }
}
