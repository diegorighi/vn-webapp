export type ApprovalStatus = 'PENDING' | 'APPROVED' | 'REJECTED';
export type ApprovalEntityType = 'CLIENTE' | 'VIAGEM' | 'MILHA' | 'PROGRAMA';
export type ApprovalActionType = 'INSERT' | 'UPDATE' | 'DELETE';

export interface ApprovalRequest {
  id: string;
  entityType: ApprovalEntityType;
  actionType: ApprovalActionType;
  entityId: string | null; // null para INSERT
  entityName: string; // Nome para exibicao (ex: nome do cliente)
  status: ApprovalStatus;
  requestedBy: ApprovalUser;
  requestedAt: string;
  reviewedBy: ApprovalUser | null;
  reviewedAt: string | null;
  rejectionReason: string | null;
  originalData: Record<string, unknown> | null; // Dados antes da alteracao (UPDATE/DELETE)
  proposedData: Record<string, unknown>; // Dados propostos
  tenantId: string;
}

export interface ApprovalUser {
  id: string;
  nome: string;
  email: string;
}

export interface ApprovalSummary {
  pending: number;
  approvedToday: number;
  rejectedToday: number;
}

// Roles e permissoes
export type UserRole = 'ROOT' | 'ADMIN' | 'MANAGER' | 'OPERATOR' | 'VIEWER';

export interface RolePermissions {
  canInsert: boolean;
  canUpdate: boolean;
  canDelete: boolean;
  canApprove: boolean;
  requiresApproval: boolean;
}

export const ROLE_PERMISSIONS: Record<UserRole, RolePermissions> = {
  ROOT: {
    canInsert: true,
    canUpdate: true,
    canDelete: true,
    canApprove: true,
    requiresApproval: false
  },
  ADMIN: {
    canInsert: true,
    canUpdate: true,
    canDelete: true,
    canApprove: true,
    requiresApproval: false
  },
  MANAGER: {
    canInsert: true,
    canUpdate: true,
    canDelete: false,
    canApprove: false,
    requiresApproval: true
  },
  OPERATOR: {
    canInsert: true,
    canUpdate: false,
    canDelete: false,
    canApprove: false,
    requiresApproval: true
  },
  VIEWER: {
    canInsert: false,
    canUpdate: false,
    canDelete: false,
    canApprove: false,
    requiresApproval: true
  }
};

export const ROLE_LABELS: Record<UserRole, string> = {
  ROOT: 'Super Administrador',
  ADMIN: 'Administrador',
  MANAGER: 'Gerente',
  OPERATOR: 'Operador',
  VIEWER: 'Visualizador'
};

export const ENTITY_LABELS: Record<ApprovalEntityType, string> = {
  CLIENTE: 'Cliente',
  VIAGEM: 'Viagem',
  MILHA: 'Milha',
  PROGRAMA: 'Programa'
};

export const ACTION_LABELS: Record<ApprovalActionType, string> = {
  INSERT: 'Cadastro',
  UPDATE: 'Alteracao',
  DELETE: 'Remocao'
};
