package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.TenantContextTestScenarios.HasAllPermissionsScenario;
import com.vanessaviagem.backoffice.domain.model.TenantContextTestScenarios.HasAnyPermissionScenario;
import com.vanessaviagem.backoffice.domain.model.TenantContextTestScenarios.HasPermissionScenario;
import com.vanessaviagem.backoffice.domain.model.TenantContextTestScenarios.SetContextScenario;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Provider de dados de teste para TenantContext.
 */
public final class TenantContextTestDataProvider {

    private TenantContextTestDataProvider() {
    }

    @SuppressWarnings("unused")
    public static Stream<Arguments> setContextScenarios() {
        return Stream.of(
                Arguments.of(new SetContextScenario(
                        "should set context with valid data",
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        Set.of("READ", "WRITE"),
                        true
                )),
                Arguments.of(new SetContextScenario(
                        "should set context with empty permissions",
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        Set.of(),
                        true
                )),
                Arguments.of(new SetContextScenario(
                        "should set context with single permission",
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        Set.of("ADMIN"),
                        true
                ))
        );
    }

    @SuppressWarnings("unused")
    public static Stream<Arguments> hasPermissionScenarios() {
        return Stream.of(
                Arguments.of(new HasPermissionScenario(
                        "should return true when user has permission",
                        Set.of("READ", "WRITE", "DELETE"),
                        "WRITE",
                        true
                )),
                Arguments.of(new HasPermissionScenario(
                        "should return false when user does not have permission",
                        Set.of("READ", "WRITE"),
                        "DELETE",
                        false
                )),
                Arguments.of(new HasPermissionScenario(
                        "should return false when user has no permissions",
                        Set.of(),
                        "READ",
                        false
                )),
                Arguments.of(new HasPermissionScenario(
                        "should be case sensitive",
                        Set.of("read", "write"),
                        "READ",
                        false
                ))
        );
    }

    @SuppressWarnings("unused")
    public static Stream<Arguments> hasAllPermissionsScenarios() {
        return Stream.of(
                Arguments.of(new HasAllPermissionsScenario(
                        "should return true when user has all required permissions",
                        Set.of("READ", "WRITE", "DELETE"),
                        Set.of("READ", "WRITE"),
                        true
                )),
                Arguments.of(new HasAllPermissionsScenario(
                        "should return false when user is missing one permission",
                        Set.of("READ", "WRITE"),
                        Set.of("READ", "WRITE", "DELETE"),
                        false
                )),
                Arguments.of(new HasAllPermissionsScenario(
                        "should return true when required permissions is empty",
                        Set.of("READ"),
                        Set.of(),
                        true
                )),
                Arguments.of(new HasAllPermissionsScenario(
                        "should return false when user has no permissions",
                        Set.of(),
                        Set.of("READ"),
                        false
                ))
        );
    }

    @SuppressWarnings("unused")
    public static Stream<Arguments> hasAnyPermissionScenarios() {
        return Stream.of(
                Arguments.of(new HasAnyPermissionScenario(
                        "should return true when user has at least one permission",
                        Set.of("READ"),
                        Set.of("READ", "WRITE", "DELETE"),
                        true
                )),
                Arguments.of(new HasAnyPermissionScenario(
                        "should return false when user has none of the permissions",
                        Set.of("ADMIN"),
                        Set.of("READ", "WRITE", "DELETE"),
                        false
                )),
                Arguments.of(new HasAnyPermissionScenario(
                        "should return false when checking empty set",
                        Set.of("READ", "WRITE"),
                        Set.of(),
                        false
                ))
        );
    }
}
