package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.TenantContextTestScenarios.HasAllPermissionsScenario;
import com.vanessaviagem.backoffice.domain.model.TenantContextTestScenarios.HasAnyPermissionScenario;
import com.vanessaviagem.backoffice.domain.model.TenantContextTestScenarios.HasPermissionScenario;
import com.vanessaviagem.backoffice.domain.model.TenantContextTestScenarios.SetContextScenario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TenantContext")
class TenantContextTest {

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.TenantContextTestDataProvider#setContextScenarios")
    @DisplayName("set and current")
    void shouldSetAndRetrieveContext(SetContextScenario scenario) {
        TenantContext.set(scenario.tenantId(), scenario.userId(), scenario.permissions());

        TenantContext context = TenantContext.current();

        assertNotNull(context);
        assertEquals(scenario.tenantId(), context.tenantId());
        assertEquals(scenario.userId(), context.userId());
        assertEquals(scenario.permissions(), context.permissions());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.TenantContextTestDataProvider#hasPermissionScenarios")
    @DisplayName("hasPermission")
    void shouldCheckSinglePermission(HasPermissionScenario scenario) {
        TenantContext.set(UUID.randomUUID(), UUID.randomUUID(), scenario.userPermissions());

        TenantContext context = TenantContext.current();
        boolean result = context.hasPermission(scenario.permissionToCheck());

        assertEquals(scenario.expectedResult(), result);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.TenantContextTestDataProvider#hasAllPermissionsScenarios")
    @DisplayName("hasAllPermissions")
    void shouldCheckAllPermissions(HasAllPermissionsScenario scenario) {
        TenantContext.set(UUID.randomUUID(), UUID.randomUUID(), scenario.userPermissions());

        TenantContext context = TenantContext.current();
        boolean result = context.hasAllPermissions(scenario.requiredPermissions());

        assertEquals(scenario.expectedResult(), result);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.TenantContextTestDataProvider#hasAnyPermissionScenarios")
    @DisplayName("hasAnyPermission")
    void shouldCheckAnyPermission(HasAnyPermissionScenario scenario) {
        TenantContext.set(UUID.randomUUID(), UUID.randomUUID(), scenario.userPermissions());

        TenantContext context = TenantContext.current();
        boolean result = context.hasAnyPermission(scenario.anyPermissions());

        assertEquals(scenario.expectedResult(), result);
    }

    @ParameterizedTest(name = "should throw SecurityException when context not initialized - {0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.TenantContextTestDataProvider#setContextScenarios")
    @DisplayName("current without initialization")
    void shouldThrowWhenContextNotInitialized(SetContextScenario scenario) {
        TenantContext.clear();

        assertThrows(SecurityException.class, TenantContext::current);
    }

    @ParameterizedTest(name = "should clear context - {0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.TenantContextTestDataProvider#setContextScenarios")
    @DisplayName("clear")
    void shouldClearContext(SetContextScenario scenario) {
        TenantContext.set(scenario.tenantId(), scenario.userId(), scenario.permissions());
        assertTrue(TenantContext.isPresent());

        TenantContext.clear();

        assertFalse(TenantContext.isPresent());
    }

    @ParameterizedTest(name = "isPresent should return true after set - {0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.TenantContextTestDataProvider#setContextScenarios")
    @DisplayName("isPresent")
    void shouldReturnTrueWhenPresent(SetContextScenario scenario) {
        assertFalse(TenantContext.isPresent());

        TenantContext.set(scenario.tenantId(), scenario.userId(), scenario.permissions());

        assertTrue(TenantContext.isPresent());
    }

    @ParameterizedTest(name = "permissions should be immutable - {0}")
    @MethodSource("com.vanessaviagem.backoffice.domain.model.TenantContextTestDataProvider#setContextScenarios")
    @DisplayName("permissions immutability")
    void shouldHaveImmutablePermissions(SetContextScenario scenario) {
        TenantContext.set(scenario.tenantId(), scenario.userId(), scenario.permissions());

        TenantContext context = TenantContext.current();
        Set<String> permissions = context.permissions();

        assertThrows(UnsupportedOperationException.class, () -> permissions.add("NEW_PERMISSION"));
    }
}
