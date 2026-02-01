import { Component, inject, signal, computed, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { ClienteService } from '../../services/cliente.service';
import { Cliente } from '../../models/cliente.model';

@Component({
  selector: 'app-cliente-search',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './cliente-search.component.html',
  styleUrl: './cliente-search.component.scss'
})
export class ClienteSearchComponent {
  private readonly clienteService = inject(ClienteService);
  private readonly router = inject(Router);

  readonly mode = input<'editar' | 'remover'>('editar');
  readonly clientes = this.clienteService.clientes;

  searchTerm = signal('');
  selectedCliente = signal<Cliente | null>(null);

  readonly filteredClientes = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    if (!term) return [];

    return this.clientes().filter(c =>
      c.nome.toLowerCase().includes(term) ||
      c.sobrenome.toLowerCase().includes(term) ||
      c.documentos.some(d => d.numero.includes(term)) ||
      c.contatos.some(cont => cont.valor.toLowerCase().includes(term))
    ).slice(0, 10);
  });

  readonly hasResults = computed(() => this.filteredClientes().length > 0);
  readonly showResults = computed(() => this.searchTerm().length >= 2 && !this.selectedCliente());

  onSearchChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.searchTerm.set(input.value);
    this.selectedCliente.set(null);
  }

  selectCliente(cliente: Cliente): void {
    this.selectedCliente.set(cliente);
    this.searchTerm.set(`${cliente.nome} ${cliente.sobrenome}`);
  }

  clearSearch(): void {
    this.searchTerm.set('');
    this.selectedCliente.set(null);
  }

  navigateToCliente(): void {
    const cliente = this.selectedCliente();
    if (!cliente) return;

    if (this.mode() === 'editar') {
      this.router.navigate(['/clientes', cliente.id, 'editar']);
    } else {
      this.router.navigate(['/clientes', cliente.id, 'remover']);
    }
  }

  getDocumentoPrincipal(cliente: Cliente): string {
    const doc = cliente.documentos.find(d => d.principal) || cliente.documentos[0];
    return doc?.numero || '-';
  }

  getContatoPrincipal(cliente: Cliente): string {
    const contato = cliente.contatos.find(c => c.principal) || cliente.contatos[0];
    return contato?.valor || '-';
  }

  get pageTitle(): string {
    return this.mode() === 'editar' ? 'Editar Cliente' : 'Remover/Inativar Cliente';
  }

  get pageSubtitle(): string {
    return this.mode() === 'editar'
      ? 'Busque e selecione um cliente para editar seus dados'
      : 'Busque e selecione um cliente para remover ou inativar';
  }

  get actionLabel(): string {
    return this.mode() === 'editar' ? 'Editar Cliente' : 'Remover/Inativar';
  }

  get actionIcon(): string {
    return this.mode() === 'editar' ? 'edit' : 'delete';
  }
}
