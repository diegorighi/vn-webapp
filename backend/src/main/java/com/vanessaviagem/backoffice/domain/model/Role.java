package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.RoleType;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Role entity defining permissions within an Account.
 *
 * <p>Each account has predefined system roles (OWNER, ADMIN, MANAGER, EDITOR, VIEWER)
 * that are created automatically. Additional custom roles can be created.</p>
 *
 * <p>Permission format: "resource:action" (e.g., "data:read", "users:write")</p>
 * <p>Wildcard "*" grants all permissions.</p>
 */
public record Role(
        UUID id,
        UUID accountId,
        RoleType type,
        String name,
        String description,
        Set<String> permissions,
        boolean isSystem,
        boolean canApprove,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public Role {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(accountId, "accountId is required");
        Objects.requireNonNull(type, "type is required");
        Objects.requireNonNull(name, "name is required");

        if (name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }

        if (permissions == null) {
            permissions = Set.of();
        }
    }

    /**
     * Checks if this role has a specific permission.
     *
     * @param permission the permission to check (e.g., "data:read")
     * @return true if the role has the permission or wildcard
     */
    public boolean hasPermission(String permission) {
        // Wildcard grants all
        if (permissions.contains("*")) {
            return true;
        }

        // Direct match
        if (permissions.contains(permission)) {
            return true;
        }

        // Check resource wildcard (e.g., "data:*" matches "data:read")
        String[] parts = permission.split(":");
        if (parts.length == 2) {
            String resourceWildcard = parts[0] + ":*";
            return permissions.contains(resourceWildcard);
        }

        return false;
    }

    /**
     * Checks if this role can write data (either directly or pending approval).
     */
    public boolean canWrite() {
        return hasPermission("data:write") || hasPermission("data:write:pending");
    }

    /**
     * Checks if writes from this role require approval.
     */
    public boolean requiresApproval() {
        return !hasPermission("data:write") && hasPermission("data:write:pending");
    }

    /**
     * Checks if this role can delete data.
     */
    public boolean canDelete() {
        return hasPermission("data:delete");
    }

    /**
     * Checks if this role can manage users.
     */
    public boolean canManageUsers() {
        return hasPermission("users:write");
    }
}
