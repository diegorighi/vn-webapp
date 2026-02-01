import { Injectable, inject, signal, computed } from '@angular/core';
import { AuthService } from './auth.service';
import {
  ApprovalRequest,
  ApprovalStatus,
  ApprovalEntityType,
  ApprovalActionType,
  ApprovalSummary,
  UserRole,
  ROLE_PERMISSIONS,
  RolePermissions
} from '../models/approval.model';

@Injectable({
  providedIn: 'root'
})
export class ApprovalService {
  private readonly authService = inject(AuthService);

  // Store de aprovacoes pendentes (mock - sera substituido por API)
  private readonly _approvals = signal<ApprovalRequest[]>([]);

  readonly approvals = this._approvals.asReadonly();

  readonly pendingApprovals = computed(() =>
    this._approvals().filter(a => a.status === 'PENDING')
  );

  readonly pendingCount = computed(() => this.pendingApprovals().length);

  readonly summary = computed((): ApprovalSummary => {
    const today = new Date().toISOString().split('T')[0];
    const all = this._approvals();

    return {
      pending: all.filter(a => a.status === 'PENDING').length,
      approvedToday: all.filter(a =>
        a.status === 'APPROVED' && a.reviewedAt?.startsWith(today)
      ).length,
      rejectedToday: all.filter(a =>
        a.status === 'REJECTED' && a.reviewedAt?.startsWith(today)
      ).length
    };
  });

  constructor() {
    this.loadMockData();
  }

  // Obter role do usuario atual
  getCurrentUserRole(): UserRole {
    const user = this.authService.user();
    if (!user) return 'VIEWER';

    // Mapear roles do usuario para UserRole
    if (user.roles.includes('ROOT')) return 'ROOT';
    if (user.roles.includes('ADMIN')) return 'ADMIN';
    if (user.roles.includes('MANAGER')) return 'MANAGER';
    if (user.roles.includes('OPERATOR')) return 'OPERATOR';
    return 'VIEWER';
  }

  // Obter permissoes do usuario atual
  getCurrentPermissions(): RolePermissions {
    return ROLE_PERMISSIONS[this.getCurrentUserRole()];
  }

  // Verificar se usuario pode executar acao diretamente (sem aprovacao)
  canExecuteDirectly(action: 'insert' | 'update' | 'delete'): boolean {
    const permissions = this.getCurrentPermissions();

    switch (action) {
      case 'insert':
        return permissions.canInsert && !permissions.requiresApproval;
      case 'update':
        return permissions.canUpdate && !permissions.requiresApproval;
      case 'delete':
        return permissions.canDelete && !permissions.requiresApproval;
      default:
        return false;
    }
  }

  // Verificar se usuario pode solicitar acao (com aprovacao)
  canRequest(action: 'insert' | 'update' | 'delete'): boolean {
    const permissions = this.getCurrentPermissions();

    switch (action) {
      case 'insert':
        return permissions.canInsert;
      case 'update':
        return permissions.canUpdate;
      case 'delete':
        return permissions.canDelete;
      default:
        return false;
    }
  }

  // Verificar se usuario pode aprovar
  canApprove(): boolean {
    return this.getCurrentPermissions().canApprove;
  }

  // Criar solicitacao de aprovacao
  createApprovalRequest(
    entityType: ApprovalEntityType,
    actionType: ApprovalActionType,
    entityId: string | null,
    entityName: string,
    proposedData: Record<string, unknown>,
    originalData: Record<string, unknown> | null = null
  ): ApprovalRequest {
    const user = this.authService.user();
    if (!user) throw new Error('Usuario nao autenticado');

    const request: ApprovalRequest = {
      id: crypto.randomUUID(),
      entityType,
      actionType,
      entityId,
      entityName,
      status: 'PENDING',
      requestedBy: {
        id: user.id,
        nome: user.nome,
        email: user.email
      },
      requestedAt: new Date().toISOString(),
      reviewedBy: null,
      reviewedAt: null,
      rejectionReason: null,
      originalData,
      proposedData,
      tenantId: user.tenantId
    };

    this._approvals.update(list => [...list, request]);

    return request;
  }

  // Aprovar solicitacao
  approve(requestId: string): boolean {
    if (!this.canApprove()) return false;

    const user = this.authService.user();
    if (!user) return false;

    this._approvals.update(list =>
      list.map(req => {
        if (req.id === requestId && req.status === 'PENDING') {
          return {
            ...req,
            status: 'APPROVED' as ApprovalStatus,
            reviewedBy: {
              id: user.id,
              nome: user.nome,
              email: user.email
            },
            reviewedAt: new Date().toISOString()
          };
        }
        return req;
      })
    );

    return true;
  }

  // Rejeitar solicitacao
  reject(requestId: string, reason: string): boolean {
    if (!this.canApprove()) return false;

    const user = this.authService.user();
    if (!user) return false;

    this._approvals.update(list =>
      list.map(req => {
        if (req.id === requestId && req.status === 'PENDING') {
          return {
            ...req,
            status: 'REJECTED' as ApprovalStatus,
            reviewedBy: {
              id: user.id,
              nome: user.nome,
              email: user.email
            },
            reviewedAt: new Date().toISOString(),
            rejectionReason: reason
          };
        }
        return req;
      })
    );

    return true;
  }

  // Obter solicitacao por ID
  getById(id: string): ApprovalRequest | undefined {
    return this._approvals().find(a => a.id === id);
  }

  // Obter solicitacoes por status
  getByStatus(status: ApprovalStatus): ApprovalRequest[] {
    return this._approvals().filter(a => a.status === status);
  }

  // Obter solicitacoes do usuario atual
  getMyRequests(): ApprovalRequest[] {
    const userId = this.authService.user()?.id;
    if (!userId) return [];
    return this._approvals().filter(a => a.requestedBy.id === userId);
  }

  // Mock data para desenvolvimento
  private loadMockData(): void {
    const mockApprovals: ApprovalRequest[] = [
      {
        id: crypto.randomUUID(),
        entityType: 'CLIENTE',
        actionType: 'INSERT',
        entityId: null,
        entityName: 'Carlos Eduardo Mendes',
        status: 'PENDING',
        requestedBy: {
          id: 'user-002',
          nome: 'Maria Operadora',
          email: 'maria@vanessaviagem.com.br'
        },
        requestedAt: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(),
        reviewedBy: null,
        reviewedAt: null,
        rejectionReason: null,
        originalData: null,
        proposedData: {
          nome: 'Carlos Eduardo',
          sobrenome: 'Mendes',
          dataNascimento: '1985-06-15',
          documentos: [{ tipo: 'CPF', numero: '123.456.789-00', principal: true }],
          contatos: [{ tipo: 'EMAIL', valor: 'carlos@email.com', principal: true }]
        },
        tenantId: 'tenant-001'
      },
      {
        id: crypto.randomUUID(),
        entityType: 'CLIENTE',
        actionType: 'UPDATE',
        entityId: 'cliente-001',
        entityName: 'Ana Paula Silva',
        status: 'PENDING',
        requestedBy: {
          id: 'user-003',
          nome: 'Joao Gerente',
          email: 'joao@vanessaviagem.com.br'
        },
        requestedAt: new Date(Date.now() - 30 * 60 * 1000).toISOString(),
        reviewedBy: null,
        reviewedAt: null,
        rejectionReason: null,
        originalData: {
          telefone: '(11) 99999-0000'
        },
        proposedData: {
          telefone: '(11) 98888-1111'
        },
        tenantId: 'tenant-001'
      },
      {
        id: crypto.randomUUID(),
        entityType: 'VIAGEM',
        actionType: 'INSERT',
        entityId: null,
        entityName: 'Pacote Paris - Julho 2026',
        status: 'PENDING',
        requestedBy: {
          id: 'user-002',
          nome: 'Maria Operadora',
          email: 'maria@vanessaviagem.com.br'
        },
        requestedAt: new Date(Date.now() - 5 * 60 * 60 * 1000).toISOString(),
        reviewedBy: null,
        reviewedAt: null,
        rejectionReason: null,
        originalData: null,
        proposedData: {
          destino: 'Paris, Franca',
          dataPartida: '2026-07-15',
          dataRetorno: '2026-07-25',
          valorTotal: 15000
        },
        tenantId: 'tenant-001'
      }
    ];

    this._approvals.set(mockApprovals);
  }
}
