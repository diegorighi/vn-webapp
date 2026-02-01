package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.RoleType;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Contexto do tenant atual na requisição.
 * Armazenado em ThreadLocal para acesso em qualquer camada da aplicação.
 *
 * <p>IMPORTANTE: Este contexto DEVE ser limpo após cada requisição para evitar
 * vazamento de dados entre threads (especialmente com virtual threads).</p>
 *
 * <p>Fluxo de uso:</p>
 * <ol>
 *   <li>Interceptor extrai JWT e valida token</li>
 *   <li>TenantContext.set() é chamado com dados do usuário</li>
 *   <li>Serviços usam TenantContext.current() para obter contexto</li>
 *   <li>Filter/Interceptor chama TenantContext.clear() no finally</li>
 * </ol>
 */
public final class TenantContext {

    private static final ThreadLocal<TenantContext> CONTEXT = new ThreadLocal<>();

    private final UUID accountId;
    private final UUID userId;
    private final String userEmail;
    private final Set<String> permissions;
    private final RoleType highestRole;
    private final boolean canApprove;

    private TenantContext(
            UUID accountId,
            UUID userId,
            String userEmail,
            Set<String> permissions,
            RoleType highestRole,
            boolean canApprove
    ) {
        this.accountId = Objects.requireNonNull(accountId, "accountId é obrigatório");
        this.userId = Objects.requireNonNull(userId, "userId é obrigatório");
        this.userEmail = Objects.requireNonNull(userEmail, "userEmail é obrigatório");
        this.permissions = Set.copyOf(Objects.requireNonNull(permissions, "permissions é obrigatório"));
        this.highestRole = Objects.requireNonNull(highestRole, "highestRole é obrigatório");
        this.canApprove = canApprove;
    }

    /**
     * Define o contexto do tenant para a thread atual (versão simplificada).
     *
     * @param accountId   ID da account (tenant)
     * @param userId      ID do usuário autenticado
     * @param permissions Permissões do usuário
     * @deprecated Use {@link #set(UUID, UUID, String, Set, RoleType, boolean)} instead
     */
    @Deprecated
    public static void set(UUID accountId, UUID userId, Set<String> permissions) {
        CONTEXT.set(new TenantContext(accountId, userId, "unknown", permissions, RoleType.VIEWER, false));
    }

    /**
     * Define o contexto completo do tenant para a thread atual.
     *
     * @param accountId   ID da account (tenant)
     * @param userId      ID do usuário autenticado
     * @param userEmail   Email do usuário (para auditoria)
     * @param permissions Permissões do usuário
     * @param highestRole Papel mais alto do usuário
     * @param canApprove  Se o usuário pode aprovar alterações
     */
    public static void set(
            UUID accountId,
            UUID userId,
            String userEmail,
            Set<String> permissions,
            RoleType highestRole,
            boolean canApprove
    ) {
        CONTEXT.set(new TenantContext(accountId, userId, userEmail, permissions, highestRole, canApprove));
    }

    /**
     * Obtém o contexto do tenant da thread atual.
     *
     * @return O contexto do tenant
     * @throws SecurityException Se o contexto não foi inicializado
     */
    public static TenantContext current() {
        TenantContext ctx = CONTEXT.get();
        if (ctx == null) {
            throw new SecurityException("TenantContext não inicializado - operação não autorizada");
        }
        return ctx;
    }

    /**
     * Verifica se existe um contexto de tenant na thread atual.
     *
     * @return true se existe contexto, false caso contrário
     */
    public static boolean isPresent() {
        return CONTEXT.get() != null;
    }

    /**
     * Limpa o contexto do tenant da thread atual.
     * DEVE ser chamado após cada requisição.
     */
    public static void clear() {
        CONTEXT.remove();
    }

    /**
     * @return ID da account (tenant) atual
     */
    public UUID accountId() {
        return accountId;
    }

    /**
     * @return ID do tenant atual (alias para accountId)
     * @deprecated Use {@link #accountId()} instead
     */
    @Deprecated
    public UUID tenantId() {
        return accountId;
    }

    /**
     * @return ID do usuário autenticado
     */
    public UUID userId() {
        return userId;
    }

    /**
     * @return Email do usuário autenticado
     */
    public String userEmail() {
        return userEmail;
    }

    /**
     * @return Permissões do usuário (imutável)
     */
    public Set<String> permissions() {
        return permissions;
    }

    /**
     * @return Papel mais alto do usuário
     */
    public RoleType highestRole() {
        return highestRole;
    }

    /**
     * @return true se o usuário pode aprovar alterações pendentes
     */
    public boolean canApprove() {
        return canApprove;
    }

    /**
     * Verifica se o usuário possui uma permissão específica.
     * Suporta wildcards (e.g., "*" ou "data:*").
     *
     * @param permission Nome da permissão (e.g., "data:read")
     * @return true se possui a permissão
     */
    public boolean hasPermission(String permission) {
        // Wildcard completo
        if (permissions.contains("*")) {
            return true;
        }

        // Match direto
        if (permissions.contains(permission)) {
            return true;
        }

        // Wildcard de recurso (e.g., "data:*" matches "data:read")
        String[] parts = permission.split(":");
        if (parts.length == 2) {
            return permissions.contains(parts[0] + ":*");
        }

        return false;
    }

    /**
     * Verifica se o usuário pode escrever dados.
     * Considera tanto permissão direta quanto pendente.
     *
     * @return true se pode escrever (com ou sem aprovação)
     */
    public boolean canWrite() {
        return hasPermission("data:write") || hasPermission("data:write:pending");
    }

    /**
     * Verifica se as escritas do usuário requerem aprovação.
     *
     * @return true se escritas precisam de aprovação
     */
    public boolean requiresApproval() {
        return !hasPermission("data:write") && hasPermission("data:write:pending");
    }

    /**
     * Verifica se o usuário pode deletar dados.
     *
     * @return true se pode deletar
     */
    public boolean canDelete() {
        return hasPermission("data:delete");
    }

    /**
     * Verifica se o usuário pode gerenciar outros usuários.
     *
     * @return true se pode gerenciar usuários
     */
    public boolean canManageUsers() {
        return hasPermission("users:write");
    }

    /**
     * Verifica se o usuário é admin ou superior.
     *
     * @return true se é ROOT ou ADMIN
     */
    public boolean isAdmin() {
        return highestRole == RoleType.ROOT || highestRole == RoleType.ADMIN;
    }

    /**
     * Verifica se o usuário é o root da conta.
     *
     * @return true se é ROOT
     */
    public boolean isRoot() {
        return highestRole == RoleType.ROOT;
    }

    /**
     * Verifica se o usuário possui todas as permissões especificadas.
     *
     * @param requiredPermissions Permissões requeridas
     * @return true se possui todas as permissões
     */
    public boolean hasAllPermissions(Set<String> requiredPermissions) {
        return requiredPermissions.stream().allMatch(this::hasPermission);
    }

    /**
     * Verifica se o usuário possui pelo menos uma das permissões especificadas.
     *
     * @param anyPermissions Permissões alternativas
     * @return true se possui pelo menos uma permissão
     */
    public boolean hasAnyPermission(Set<String> anyPermissions) {
        return anyPermissions.stream().anyMatch(this::hasPermission);
    }

    @Override
    public String toString() {
        return "TenantContext{" +
                "accountId=" + accountId +
                ", userId=" + userId +
                ", userEmail='" + userEmail + '\'' +
                ", highestRole=" + highestRole +
                ", canApprove=" + canApprove +
                '}';
    }
}
