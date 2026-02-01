package com.vanessaviagem.backoffice.domain.model;

import java.util.Set;
import java.util.UUID;

/**
 * Cenários de teste para TenantContext.
 */
public final class TenantContextTestScenarios {

    private TenantContextTestScenarios() {
    }

    public record SetContextScenario(
            String description,
            UUID tenantId,
            UUID userId,
            Set<String> permissions,
            boolean shouldSucceed
    ) {
        @Override
        public String toString() {
            return description;
        }
    }

    public record HasPermissionScenario(
            String description,
            Set<String> userPermissions,
            String permissionToCheck,
            boolean expectedResult
    ) {
        @Override
        public String toString() {
            return description;
        }
    }

    public record HasAllPermissionsScenario(
            String description,
            Set<String> userPermissions,
            Set<String> requiredPermissions,
            boolean expectedResult
    ) {
        @Override
        public String toString() {
            return description;
        }
    }

    public record HasAnyPermissionScenario(
            String description,
            Set<String> userPermissions,
            Set<String> anyPermissions,
            boolean expectedResult
    ) {
        @Override
        public String toString() {
            return description;
        }
    }
}
