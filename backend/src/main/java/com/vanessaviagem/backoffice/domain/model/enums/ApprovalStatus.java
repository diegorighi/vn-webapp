package com.vanessaviagem.backoffice.domain.model.enums;

/**
 * Status of a data approval request.
 *
 * <p>Workflow:</p>
 * <pre>
 *   INSERT by non-admin → PENDING → (admin review) → APPROVED / REJECTED
 *   INSERT by admin     → AUTO_APPROVED (immediate)
 * </pre>
 */
public enum ApprovalStatus {
    /**
     * Waiting for admin/owner review.
     * Data is only visible to creator and approvers.
     */
    PENDING,

    /**
     * Approved by an admin/owner.
     * Data is visible to all users in the account.
     */
    APPROVED,

    /**
     * Rejected by an admin/owner.
     * Data is not applied and remains in pending state for reference.
     */
    REJECTED,

    /**
     * Automatically approved (creator has approval rights).
     * Data is immediately visible to all users.
     */
    AUTO_APPROVED;

    /**
     * Checks if this status represents approved data (visible to all).
     */
    public boolean isApproved() {
        return this == APPROVED || this == AUTO_APPROVED;
    }

    /**
     * Checks if this status is terminal (cannot change).
     */
    public boolean isFinal() {
        return this == APPROVED || this == REJECTED || this == AUTO_APPROVED;
    }
}
