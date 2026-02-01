package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * User entity belonging to an Account.
 *
 * <p>A User can have multiple roles within their account, determining
 * their permissions and capabilities.</p>
 *
 * <p>Key invariants:</p>
 * <ul>
 *   <li>Email is unique per account</li>
 *   <li>Password must be hashed (never plaintext)</li>
 *   <li>User is always associated with exactly one account</li>
 * </ul>
 */
public record User(
        UUID id,
        UUID accountId,
        String email,
        String passwordHash,
        String name,
        String avatarUrl,
        String phone,
        UserStatus status,
        LocalDateTime emailVerifiedAt,
        LocalDateTime lastLoginAt,
        int failedLoginAttempts,
        LocalDateTime lockedUntil,
        Map<String, Object> preferences,
        String timezone,
        String locale,
        Set<Role> roles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 30;

    public User {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(accountId, "accountId is required");
        Objects.requireNonNull(email, "email is required");
        Objects.requireNonNull(passwordHash, "passwordHash is required");
        Objects.requireNonNull(name, "name is required");
        Objects.requireNonNull(status, "status is required");

        if (name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("email must be valid");
        }
        if (failedLoginAttempts < 0) {
            throw new IllegalArgumentException("failedLoginAttempts cannot be negative");
        }

        if (preferences == null) {
            preferences = Map.of();
        }
        if (timezone == null) {
            timezone = "America/Sao_Paulo";
        }
        if (locale == null) {
            locale = "pt-BR";
        }
        if (roles == null) {
            roles = Set.of();
        }
    }

    /**
     * Creates a new pending user (requires email verification).
     */
    public static User criar(UUID accountId, String email, String passwordHash, String name) {
        LocalDateTime now = LocalDateTime.now();
        return new User(
                UUID.randomUUID(),
                accountId,
                email.trim().toLowerCase(),
                passwordHash,
                name.trim(),
                null,
                null,
                UserStatus.PENDING_ACTIVATION,
                null,
                null,
                0,
                null,
                Map.of(),
                "America/Sao_Paulo",
                "pt-BR",
                Set.of(),
                now,
                now
        );
    }

    /**
     * Activates the user after email verification.
     */
    public User activate() {
        return new User(
                id, accountId, email, passwordHash, name, avatarUrl, phone,
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                lastLoginAt, 0, null,
                preferences, timezone, locale, roles,
                createdAt, LocalDateTime.now()
        );
    }

    /**
     * Records a successful login.
     */
    public User recordSuccessfulLogin() {
        return new User(
                id, accountId, email, passwordHash, name, avatarUrl, phone,
                status, emailVerifiedAt,
                LocalDateTime.now(),
                0,
                null,
                preferences, timezone, locale, roles,
                createdAt, LocalDateTime.now()
        );
    }

    /**
     * Records a failed login attempt.
     */
    public User recordFailedLogin() {
        int newAttempts = failedLoginAttempts + 1;
        LocalDateTime newLockedUntil = null;

        if (newAttempts >= MAX_FAILED_ATTEMPTS) {
            newLockedUntil = LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES);
        }

        return new User(
                id, accountId, email, passwordHash, name, avatarUrl, phone,
                status, emailVerifiedAt, lastLoginAt,
                newAttempts,
                newLockedUntil,
                preferences, timezone, locale, roles,
                createdAt, LocalDateTime.now()
        );
    }

    /**
     * Checks if the user is locked out.
     */
    public boolean isLocked() {
        return lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now());
    }

    /**
     * Checks if the user can log in.
     */
    public boolean canLogin() {
        return status == UserStatus.ACTIVE && !isLocked();
    }

    /**
     * Checks if the user has a specific permission.
     */
    public boolean hasPermission(String permission) {
        return roles.stream()
                .anyMatch(role -> role.hasPermission(permission));
    }

    /**
     * Checks if the user can approve pending changes.
     */
    public boolean canApprove() {
        return roles.stream()
                .anyMatch(Role::canApprove);
    }

    /**
     * Checks if the user has any of the specified permissions.
     */
    public boolean hasAnyPermission(String... permissions) {
        for (String permission : permissions) {
            if (hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }
}
