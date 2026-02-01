import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ClienteService } from '../../services/cliente.service';
import { Cliente, ClienteStatus, Documento, Endereco, Contato } from '../../models/cliente.model';

@Component({
  selector: 'app-cliente-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './cliente-list.component.html',
  styleUrl: './cliente-list.component.scss'
})
export class ClienteListComponent implements OnInit {
  private readonly clienteService = inject(ClienteService);

  readonly clientes = this.clienteService.clientes;
  readonly totalClientes = this.clienteService.totalClientes;
  readonly clientesAtivos = this.clienteService.clientesAtivos;
  readonly clientesInativos = this.clienteService.clientesInativos;
  readonly isLoading = this.clienteService.isLoading;

  ngOnInit(): void {
    this.clienteService.carregarClientes();
  }

  searchTerm = signal('');
  statusFilter = signal<ClienteStatus | 'TODOS'>('TODOS');
  clienteToDelete = signal<Cliente | null>(null);
  showDeleteModal = signal(false);
  clienteToView = signal<Cliente | null>(null);
  showViewModal = signal(false);
  imageModalUrl = signal<string>('');
  imageModalTitle = signal<string>('');
  showImageModal = signal(false);
  activeTab = signal<'dados' | 'viagens'>('dados');

  // Mock de viagens do cliente
  readonly viagens = signal([
    {
      id: '1',
      origem: 'GRU',
      destino: 'MIA',
      dataViagem: '2024-03-15',
      status: 'CONFIRMADA',
      companhia: 'LATAM',
      classe: 'Executiva',
      milhas: 50000,
      valor: 'R$ 3.450,00'
    },
    {
      id: '2',
      origem: 'CGH',
      destino: 'SDU',
      dataViagem: '2024-02-20',
      status: 'REALIZADA',
      companhia: 'GOL',
      classe: 'Econômica',
      milhas: 8000,
      valor: 'R$ 450,00'
    },
    {
      id: '3',
      origem: 'GRU',
      destino: 'LIS',
      dataViagem: '2024-05-10',
      status: 'PENDENTE',
      companhia: 'TAP',
      classe: 'Business',
      milhas: 75000,
      valor: 'R$ 5.200,00'
    }
  ]);

  get filteredClientes(): Cliente[] {
    let result = this.clientes();

    if (this.statusFilter() !== 'TODOS') {
      result = result.filter(c => c.status === this.statusFilter());
    }

    const term = this.searchTerm().toLowerCase().trim();
    if (term) {
      result = result.filter(c =>
        c.nome.toLowerCase().includes(term) ||
        c.sobrenome.toLowerCase().includes(term) ||
        c.contatos.some(cont => cont.valor.toLowerCase().includes(term)) ||
        c.documentos.some(d => d.numero.includes(term))
      );
    }

    return result;
  }

  getDocumentoPrincipal(cliente: Cliente): Documento | undefined {
    return cliente.documentos.find(d => d.principal) || cliente.documentos[0];
  }

  getEnderecoPrincipal(cliente: Cliente): Endereco | undefined {
    return cliente.enderecos.find(e => e.principal) || cliente.enderecos[0];
  }

  getContatoEmail(cliente: Cliente): string {
    const email = cliente.contatos.find(c => c.tipo === 'EMAIL');
    return email?.valor || '-';
  }

  getContatoTelefone(cliente: Cliente): string {
    const telefone = cliente.contatos.find(c => c.tipo === 'CELULAR' || c.tipo === 'TELEFONE' || c.tipo === 'WHATSAPP');
    return telefone?.valor || '-';
  }

  onSearchChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.searchTerm.set(input.value);
  }

  onStatusFilterChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.statusFilter.set(select.value as ClienteStatus | 'TODOS');
  }

  getStatusClass(status: ClienteStatus): string {
    const classes: Record<ClienteStatus, string> = {
      'ATIVO': 'status--ativo',
      'INATIVO': 'status--inativo',
      'PENDENTE': 'status--pendente'
    };
    return classes[status];
  }

  getStatusLabel(status: ClienteStatus): string {
    const labels: Record<ClienteStatus, string> = {
      'ATIVO': 'Ativo',
      'INATIVO': 'Inativo',
      'PENDENTE': 'Pendente'
    };
    return labels[status];
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('pt-BR');
  }

  confirmDelete(cliente: Cliente): void {
    this.clienteToDelete.set(cliente);
    this.showDeleteModal.set(true);
  }

  cancelDelete(): void {
    this.clienteToDelete.set(null);
    this.showDeleteModal.set(false);
  }

  async executeDelete(): Promise<void> {
    const cliente = this.clienteToDelete();
    if (cliente) {
      await this.clienteService.delete(cliente.id);
      this.cancelDelete();
    }
  }

  async toggleStatus(cliente: Cliente): Promise<void> {
    const newStatus: ClienteStatus = cliente.status === 'ATIVO' ? 'INATIVO' : 'ATIVO';
    await this.clienteService.updateStatus(cliente.id, newStatus);
  }

  viewCliente(cliente: Cliente): void {
    this.clienteToView.set(cliente);
    this.showViewModal.set(true);
  }

  closeViewModal(): void {
    this.clienteToView.set(null);
    this.showViewModal.set(false);
    this.activeTab.set('dados'); // Reset para aba inicial
  }

  setActiveTab(tab: 'dados' | 'viagens'): void {
    this.activeTab.set(tab);
  }

  getViagemStatusClass(status: string): string {
    const classes: Record<string, string> = {
      'CONFIRMADA': 'status--success',
      'REALIZADA': 'status--info',
      'PENDENTE': 'status--warning',
      'CANCELADA': 'status--error'
    };
    return classes[status] || 'status--default';
  }

  getViagemStatusLabel(status: string): string {
    const labels: Record<string, string> = {
      'CONFIRMADA': 'Confirmada',
      'REALIZADA': 'Realizada',
      'PENDENTE': 'Pendente',
      'CANCELADA': 'Cancelada'
    };
    return labels[status] || status;
  }

  getTipoDocumentoLabel(tipo: string): string {
    const labels: Record<string, string> = {
      'CPF': 'CPF',
      'RG': 'RG',
      'CNH': 'CNH',
      'PASSAPORTE': 'Passaporte'
    };
    return labels[tipo] || tipo;
  }

  getTipoContatoLabel(tipo: string): string {
    const labels: Record<string, string> = {
      'EMAIL': 'E-mail',
      'CELULAR': 'Celular',
      'TELEFONE': 'Telefone',
      'WHATSAPP': 'WhatsApp'
    };
    return labels[tipo] || tipo;
  }

  getTipoEnderecoLabel(tipo: string): string {
    const labels: Record<string, string> = {
      'RESIDENCIAL': 'Residencial',
      'COMERCIAL': 'Comercial',
      'COBRANCA': 'Cobrança'
    };
    return labels[tipo] || tipo;
  }

  openImageModal(url: string, tipo: string): void {
    this.imageModalUrl.set(url);
    this.imageModalTitle.set(`Digitalização do ${this.getTipoDocumentoLabel(tipo)}`);
    this.showImageModal.set(true);
  }

  closeImageModal(): void {
    this.imageModalUrl.set('');
    this.imageModalTitle.set('');
    this.showImageModal.set(false);
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.style.display = 'none';
    const parent = img.parentElement;
    if (parent) {
      parent.classList.add('doc-preview__image--error');
    }
  }
}
