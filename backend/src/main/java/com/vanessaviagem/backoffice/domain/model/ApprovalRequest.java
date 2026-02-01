package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.ApprovalStatus;
import com.vanessaviagem.backoffice.domain.model.enums.EntityType;
import com.vanessaviagem.backoffice.domain.model.enums.OperationType;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Approval request for data changes (similar to a Git Pull Request).
 *
 * <p>When a non-admin user creates, updates, or deletes data, an approval request
 * is created. The data remains in PENDING state until an admin approves or rejects it.</p>
 *
 * <p>Flow:</p>
 * <ol>
 *   <li>User creates/modifies data → ApprovalRequest created with PENDING status</li>
 *   <li>Data stored with approval_status = PENDING (invisible to other users)</li>
 *   <li>Admin reviews the request</li>
 *   <li>On APPROVE: Data becomes visible (approval_status = APPROVED)</li>
 *   <li>On REJECT: Data remains invisible, request marked as REJECTED</li>
 * </ol>
 */
public record ApprovalRequest(
        UUID id,
        UUID accountId,
        EntityType entityType,
        UUID entityId,
        OperationType operation,
        Map<String, Object> dataBefore,
        Map<String, Object> dataAfter,
        UUID requestedBy,
        LocalDateTime requestedAt,
        String reason,
        ApprovalStatus status,
        UUID reviewedBy,
        LocalDateTime reviewedAt,
        String reviewComment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public ApprovalRequest {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(accountId, "accountId is required");
        Objects.requireNonNull(entityType, "entityType is required");
        Objects.requireNonNull(operation, "operation is required");
        Objects.requireNonNull(dataAfter, "dataAfter is required");
        Objects.requireNonNull(requestedBy, "requestedBy is required");
        Objects.requireNonNull(status, "status is required");

        // Validate operation constraints
        if (operation == OperationType.INSERT && entityId != null) {
            throw new IllegalArgumentException("entityId must be null for INSERT operations");
        }
        if (operation == OperationType.INSERT && dataBefore != null) {
            throw new IllegalArgumentException("dataBefore must be null for INSERT operations");
        }
        if ((operation == OperationType.UPDATE || operation == OperationType.DELETE) && entityId == null) {
            throw new IllegalArgumentException("entityId is required for UPDATE/DELETE operations");
        }
    }

    /**
     * Creates a new approval request for an INSERT operation.
     */
    public static ApprovalRequest criarParaInsert(
            UUID accountId,
            EntityType entityType,
            Map<String, Object> data,
            UUID requestedBy,
            String reason
    ) {
        LocalDateTime now = LocalDateTime.now();
        return new ApprovalRequest(
                UUID.randomUUID(),
                accountId,
                entityType,
                null,  // No entity yet
                OperationType.INSERT,
                null,  // No previous state
                data,
                requestedBy,
                now,
                reason,
                ApprovalStatus.PENDING,
                null,
                null,
                null,
                now,
                now
        );
    }

    /**
     * Creates a new approval request for an UPDATE operation.
     */
    public static ApprovalRequest criarParaUpdate(
            UUID accountId,
            EntityType entityType,
            UUID entityId,
            Map<String, Object> dataBefore,
            Map<String, Object> dataAfter,
            UUID requestedBy,
            String reason
    ) {
        LocalDateTime now = LocalDateTime.now();
        return new ApprovalRequest(
                UUID.randomUUID(),
                accountId,
                entityType,
                entityId,
                OperationType.UPDATE,
                dataBefore,
                dataAfter,
                requestedBy,
                now,
                reason,
                ApprovalStatus.PENDING,
                null,
                null,
                null,
                now,
                now
        );
    }

    /**
     * Creates a new approval request for a DELETE operation.
     */
    public static ApprovalRequest criarParaDelete(
            UUID accountId,
            EntityType entityType,
            UUID entityId,
            Map<String, Object> dataBefore,
            UUID requestedBy,
            String reason
    ) {
        LocalDateTime now = LocalDateTime.now();
        return new ApprovalRequest(
                UUID.randomUUID(),
                accountId,
                entityType,
                entityId,
                OperationType.DELETE,
                dataBefore,
                Map.of("deleted", true),  // Marker for deletion
                requestedBy,
                now,
                reason,
                ApprovalStatus.PENDING,
                null,
                null,
                null,
                now,
                now
        );
    }

    /**
     * Approves the request.
     */
    public ApprovalRequest approve(UUID reviewerId, String comment) {
        if (status != ApprovalStatus.PENDING) {
            throw new IllegalStateException("Only PENDING requests can be approved");
        }

        return new ApprovalRequest(
                id, accountId, entityType, entityId, operation,
                dataBefore, dataAfter,
                requestedBy, requestedAt, reason,
                ApprovalStatus.APPROVED,
                reviewerId,
                LocalDateTime.now(),
                comment,
                createdAt, LocalDateTime.now()
        );
    }

    /**
     * Rejects the request.
     */
    public ApprovalRequest reject(UUID reviewerId, String comment) {
        if (status != ApprovalStatus.PENDING) {
            throw new IllegalStateException("Only PENDING requests can be rejected");
        }

        return new ApprovalRequest(
                id, accountId, entityType, entityId, operation,
                dataBefore, dataAfter,
                requestedBy, requestedAt, reason,
                ApprovalStatus.REJECTED,
                reviewerId,
                LocalDateTime.now(),
                comment,
                createdAt, LocalDateTime.now()
        );
    }

    /**
     * Checks if this request is pending review.
     */
    public boolean isPending() {
        return status == ApprovalStatus.PENDING;
    }

    /**
     * Checks if this request was approved.
     */
    public boolean isApproved() {
        return status.isApproved();
    }
}
