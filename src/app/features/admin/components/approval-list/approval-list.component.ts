import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApprovalService } from '../../../../core/services/approval.service';
import { ToastService } from '../../../../shared/services/toast.service';
import {
  ApprovalRequest,
  ApprovalStatus,
  ENTITY_LABELS,
  ACTION_LABELS
} from '../../../../core/models/approval.model';

@Component({
  selector: 'app-approval-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './approval-list.component.html',
  styleUrl: './approval-list.component.scss'
})
export class ApprovalListComponent {
  private readonly approvalService = inject(ApprovalService);
  private readonly toastService = inject(ToastService);

  readonly canApprove = this.approvalService.canApprove();
  readonly summary = this.approvalService.summary;
  readonly pendingApprovals = this.approvalService.pendingApprovals;

  readonly selectedFilter = signal<ApprovalStatus | 'ALL'>('PENDING');
  readonly selectedApproval = signal<ApprovalRequest | null>(null);
  readonly showRejectModal = signal(false);
  readonly rejectReason = signal('');

  readonly filteredApprovals = computed(() => {
    const filter = this.selectedFilter();
    const all = this.approvalService.approvals();

    if (filter === 'ALL') return all;
    return all.filter(a => a.status === filter);
  });

  readonly entityLabels = ENTITY_LABELS;
  readonly actionLabels = ACTION_LABELS;

  setFilter(filter: ApprovalStatus | 'ALL'): void {
    this.selectedFilter.set(filter);
  }

  selectApproval(approval: ApprovalRequest): void {
    this.selectedApproval.set(approval);
  }

  closeDetail(): void {
    this.selectedApproval.set(null);
  }

  approve(approval: ApprovalRequest): void {
    if (!this.canApprove) {
      this.toastService.error('Voce nao tem permissao para aprovar');
      return;
    }

    const success = this.approvalService.approve(approval.id);
    if (success) {
      this.toastService.success('Solicitacao aprovada com sucesso!');
      this.selectedApproval.set(null);
    } else {
      this.toastService.error('Erro ao aprovar solicitacao');
    }
  }

  openRejectModal(approval: ApprovalRequest): void {
    this.selectedApproval.set(approval);
    this.showRejectModal.set(true);
    this.rejectReason.set('');
  }

  closeRejectModal(): void {
    this.showRejectModal.set(false);
    this.rejectReason.set('');
  }

  onRejectReasonChange(event: Event): void {
    const input = event.target as HTMLTextAreaElement;
    this.rejectReason.set(input.value);
  }

  confirmReject(): void {
    const approval = this.selectedApproval();
    const reason = this.rejectReason();

    if (!approval) return;

    if (!reason.trim()) {
      this.toastService.warning('Informe o motivo da rejeicao');
      return;
    }

    const success = this.approvalService.reject(approval.id, reason);
    if (success) {
      this.toastService.success('Solicitacao rejeitada');
      this.closeRejectModal();
      this.selectedApproval.set(null);
    } else {
      this.toastService.error('Erro ao rejeitar solicitacao');
    }
  }

  getStatusClass(status: ApprovalStatus): string {
    const classes: Record<ApprovalStatus, string> = {
      PENDING: 'status--pending',
      APPROVED: 'status--approved',
      REJECTED: 'status--rejected'
    };
    return classes[status];
  }

  getStatusLabel(status: ApprovalStatus): string {
    const labels: Record<ApprovalStatus, string> = {
      PENDING: 'Pendente',
      APPROVED: 'Aprovado',
      REJECTED: 'Rejeitado'
    };
    return labels[status];
  }

  getActionClass(action: string): string {
    const classes: Record<string, string> = {
      INSERT: 'action--insert',
      UPDATE: 'action--update',
      DELETE: 'action--delete'
    };
    return classes[action] || '';
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getTimeSince(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMins / 60);
    const diffDays = Math.floor(diffHours / 24);

    if (diffMins < 60) return `${diffMins} min atras`;
    if (diffHours < 24) return `${diffHours}h atras`;
    return `${diffDays}d atras`;
  }

  getInitials(nome: string): string {
    return nome.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase();
  }

  formatDataForDisplay(data: Record<string, unknown>): { key: string; value: string }[] {
    return Object.entries(data).map(([key, value]) => ({
      key: this.formatKey(key),
      value: this.formatValue(value)
    }));
  }

  private formatKey(key: string): string {
    const labels: Record<string, string> = {
      nome: 'Nome',
      sobrenome: 'Sobrenome',
      dataNascimento: 'Data de Nascimento',
      telefone: 'Telefone',
      email: 'E-mail',
      documentos: 'Documentos',
      contatos: 'Contatos',
      destino: 'Destino',
      dataPartida: 'Data de Partida',
      dataRetorno: 'Data de Retorno',
      valorTotal: 'Valor Total'
    };
    return labels[key] || key;
  }

  private formatValue(value: unknown): string {
    if (value === null || value === undefined) return '-';
    if (typeof value === 'number') {
      return value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
    }
    if (Array.isArray(value)) {
      return `${value.length} item(s)`;
    }
    if (typeof value === 'object') {
      return JSON.stringify(value);
    }
    return String(value);
  }
}
