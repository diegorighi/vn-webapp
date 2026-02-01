package com.vanessaviagem.backoffice.domain.model.enums;

/**
 * Predefined role types for accounts.
 *
 * <p>Hierarchy (highest to lowest privilege):</p>
 * <pre>
 * ┌──────────┬────────┬────────┬────────┬─────────┬──────────────┐
 * │ Role     │ Insert │ Update │ Delete │ Approve │ Requer Aprov │
 * ├──────────┼────────┼────────┼────────┼─────────┼──────────────┤
 * │ ROOT     │   ✓    │   ✓    │   ✓    │    ✓    │      ✗       │
 * │ ADMIN    │   ✓    │   ✓    │   ✓    │    ✓    │      ✗       │
 * │ MANAGER  │   ✓    │   ✓    │   ✗    │    ✗    │      ✓       │
 * │ OPERATOR │   ✓    │   ✗    │   ✗    │    ✗    │      ✓       │
 * │ VIEWER   │   ✗    │   ✗    │   ✗    │    ✗    │      ✓       │
 * └──────────┴────────┴────────┴────────┴─────────┴──────────────┘
 * </pre>
 */
public enum RoleType {
    /**
     * Root/Super Admin with full control.
     * Can do everything including manage billing and delete account.
     * Insert ✓, Update ✓, Delete ✓, Approve ✓, Requires Approval ✗
     */
    ROOT,

    /**
     * Administrator who can manage users and approve changes.
     * Insert ✓, Update ✓, Delete ✓, Approve ✓, Requires Approval ✗
     */
    ADMIN,

    /**
     * Manager who can insert and update data.
     * Cannot delete. All changes require approval.
     * Insert ✓, Update ✓, Delete ✗, Approve ✗, Requires Approval ✓
     */
    MANAGER,

    /**
     * Operator who can only insert data.
     * Cannot update or delete. All changes require approval.
     * Insert ✓, Update ✗, Delete ✗, Approve ✗, Requires Approval ✓
     */
    OPERATOR,

    /**
     * Viewer with read-only access.
     * Cannot make any changes.
     * Insert ✗, Update ✗, Delete ✗, Approve ✗, Requires Approval ✓
     */
    VIEWER;

    /**
     * Checks if this role type has higher or equal privilege than another.
     */
    public boolean isAtLeast(RoleType other) {
        return this.ordinal() <= other.ordinal();
    }

    /**
     * Checks if this role type has higher privilege than another.
     */
    public boolean isHigherThan(RoleType other) {
        return this.ordinal() < other.ordinal();
    }
}
