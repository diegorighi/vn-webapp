package com.vanessaviagem.backoffice.domain.model.enums;

/**
 * Status of an Account (tenant).
 */
public enum AccountStatus {
    /**
     * Account is fully operational.
     */
    ACTIVE,

    /**
     * Account is in trial period (limited time).
     */
    TRIAL,

    /**
     * Account is suspended (e.g., payment issues).
     * Data is preserved but access is restricted.
     */
    SUSPENDED,

    /**
     * Account is blocked (e.g., ToS violation).
     * Requires admin intervention to unblock.
     */
    BLOCKED
}
