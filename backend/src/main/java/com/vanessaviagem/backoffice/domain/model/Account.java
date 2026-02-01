package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.AccountStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Account (Tenant) aggregate root.
 *
 * <p>An Account represents an organization/company that owns all data within it.
 * All business data (customers, miles, transactions) belongs to exactly one account.</p>
 *
 * <p>Key invariants:</p>
 * <ul>
 *   <li>Slug must be unique and URL-friendly</li>
 *   <li>Email must be valid</li>
 *   <li>Max users cannot be exceeded</li>
 * </ul>
 */
public record Account(
        UUID id,
        String name,
        String slug,
        String cnpj,
        String email,
        String phone,
        AccountStatus status,
        int maxUsers,
        Map<String, Object> settings,
        String plan,
        LocalDateTime trialEndsAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public Account {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(name, "name is required");
        Objects.requireNonNull(slug, "slug is required");
        Objects.requireNonNull(email, "email is required");
        Objects.requireNonNull(status, "status is required");

        if (name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        if (!slug.matches("^[a-z0-9-]+$")) {
            throw new IllegalArgumentException("slug must contain only lowercase letters, numbers, and hyphens");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("email must be valid");
        }
        if (maxUsers < 1) {
            throw new IllegalArgumentException("maxUsers must be at least 1");
        }

        if (settings == null) {
            settings = Map.of();
        }
        if (plan == null) {
            plan = "FREE";
        }
    }

    /**
     * Creates a new account with trial status.
     */
    public static Account criar(String name, String slug, String email, String cnpj) {
        LocalDateTime now = LocalDateTime.now();
        return new Account(
                UUID.randomUUID(),
                name.trim(),
                slug.toLowerCase().trim(),
                cnpj,
                email.trim().toLowerCase(),
                null,
                AccountStatus.TRIAL,
                5,
                Map.of(),
                "FREE",
                now.plusDays(14), // 14-day trial
                now,
                now
        );
    }

    /**
     * Activates the account (after trial or payment).
     */
    public Account activate() {
        return new Account(
                id, name, slug, cnpj, email, phone,
                AccountStatus.ACTIVE,
                maxUsers, settings, plan, null,
                createdAt, LocalDateTime.now()
        );
    }

    /**
     * Suspends the account (e.g., payment issues).
     */
    public Account suspend() {
        return new Account(
                id, name, slug, cnpj, email, phone,
                AccountStatus.SUSPENDED,
                maxUsers, settings, plan, trialEndsAt,
                createdAt, LocalDateTime.now()
        );
    }

    /**
     * Blocks the account (e.g., ToS violation).
     */
    public Account block() {
        return new Account(
                id, name, slug, cnpj, email, phone,
                AccountStatus.BLOCKED,
                maxUsers, settings, plan, trialEndsAt,
                createdAt, LocalDateTime.now()
        );
    }

    /**
     * Checks if the account is operational (can access data).
     */
    public boolean isOperational() {
        return status == AccountStatus.ACTIVE ||
               (status == AccountStatus.TRIAL && trialEndsAt != null && trialEndsAt.isAfter(LocalDateTime.now()));
    }

    /**
     * Checks if the account can add more users.
     */
    public boolean canAddUser(int currentUserCount) {
        return currentUserCount < maxUsers;
    }
}
