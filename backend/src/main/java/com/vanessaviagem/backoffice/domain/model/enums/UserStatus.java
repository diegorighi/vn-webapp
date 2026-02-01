package com.vanessaviagem.backoffice.domain.model.enums;

/**
 * Status of a User within an Account.
 */
public enum UserStatus {
    /**
     * User is fully operational.
     */
    ACTIVE,

    /**
     * User has not yet verified their email.
     */
    PENDING_ACTIVATION,

    /**
     * User has been deactivated (soft delete).
     */
    INACTIVE,

    /**
     * User is blocked (e.g., security concern).
     */
    BLOCKED
}
