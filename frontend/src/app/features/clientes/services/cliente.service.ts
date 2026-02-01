import { Injectable, signal, computed, inject } from '@angular/core';
import { Cliente, ClienteFormData, ClienteStatus, Documento, Endereco, Contato } from '../models/cliente.model';
import { GraphQLService } from '../../../core/services/graphql.service';
import { ToastService } from '../../../shared/services/toast.service';
import { firstValueFrom } from 'rxjs';

// GraphQL Response Types
interface ClienteGQL {
  id: string;
  nome: string;
  sobrenome: string;
  dataNascimento: string | null;
  status: ClienteStatus;
  observacoes: string | null;
  criadoEm: string | null;
  atualizadoEm: string | null;
  documentos: DocumentoGQL[];
  enderecos: EnderecoGQL[];
  contatos: ContatoGQL[];
}

interface DocumentoGQL {
  id: string;
  tipo: string;
  numero: string;
  principal: boolean;
  arquivoUrl: string | null;
  nomeArquivo: string | null;
}

interface EnderecoGQL {
  id: string;
  tipo: string;
  cep: string;
  logradouro: string;
  numero: string;
  complemento: string | null;
  bairro: string;
  cidade: string;
  estado: string;
  principal: boolean;
}

interface ContatoGQL {
  id: string;
  tipo: string;
  valor: string;
  principal: boolean;
}

interface ResumoClientesGQL {
  totalClientes: number;
  ativos: number;
  inativos: number;
  pendentes: number;
  clientes: ClienteGQL[];
}

// GraphQL Queries
const QUERIES = {
  RESUMO_CLIENTES: `
    query ResumoClientes {
      resumoClientes {
        totalClientes
        ativos
        inativos
        pendentes
        clientes {
          id
          nome
          sobrenome
          dataNascimento
          status
          observacoes
          documentos {
            id
            tipo
            numero
            principal
            arquivoUrl
            nomeArquivo
          }
          enderecos {
            id
            tipo
            cep
            logradouro
            numero
            complemento
            bairro
            cidade
            estado
            principal
          }
          contatos {
            id
            tipo
            valor
            principal
          }
        }
      }
    }
  `,

  CLIENTE_BY_ID: `
    query ClienteById($id: ID!) {
      clienteById(id: $id) {
        id
        nome
        sobrenome
        dataNascimento
        status
        observacoes
        documentos {
          id
          tipo
          numero
          principal
          arquivoUrl
          nomeArquivo
        }
        enderecos {
          id
          tipo
          cep
          logradouro
          numero
          complemento
          bairro
          cidade
          estado
          principal
        }
        contatos {
          id
          tipo
          valor
          principal
        }
      }
    }
  `,

  CLIENTES_POR_STATUS: `
    query ClientesPorStatus($status: StatusCliente!) {
      clientesPorStatus(status: $status) {
        id
        nome
        sobrenome
        status
        documentos {
          tipo
          numero
          principal
        }
        contatos {
          tipo
          valor
          principal
        }
        enderecos {
          cidade
          estado
          principal
        }
      }
    }
  `
};

// GraphQL Mutations
const MUTATIONS = {
  CRIAR_CLIENTE: `
    mutation CriarCliente($input: CriarClienteInput!) {
      criarCliente(input: $input) {
        id
        nome
        sobrenome
        status
      }
    }
  `,

  ATUALIZAR_CLIENTE: `
    mutation AtualizarCliente($input: AtualizarClienteInput!) {
      atualizarCliente(input: $input) {
        id
        nome
        sobrenome
        status
      }
    }
  `,

  ALTERAR_STATUS: `
    mutation AlterarStatusCliente($id: ID!, $status: StatusCliente!) {
      alterarStatusCliente(id: $id, status: $status) {
        id
        status
      }
    }
  `,

  EXCLUIR_CLIENTE: `
    mutation ExcluirCliente($id: ID!) {
      excluirCliente(id: $id)
    }
  `
};

@Injectable({
  providedIn: 'root'
})
export class ClienteService {
  private readonly graphql = inject(GraphQLService);
  private readonly toastService = inject(ToastService);

  private readonly _clientes = signal<Cliente[]>([]);
  private readonly _resumo = signal<{ total: number; ativos: number; inativos: number; pendentes: number } | null>(null);
  private readonly _isLoading = signal(false);
  private readonly _error = signal<string | null>(null);

  readonly clientes = this._clientes.asReadonly();
  readonly isLoading = this._isLoading.asReadonly();
  readonly error = this._error.asReadonly();

  readonly totalClientes = computed(() => this._resumo()?.total ?? 0);
  readonly clientesAtivos = computed(() => this._resumo()?.ativos ?? 0);
  readonly clientesInativos = computed(() => this._resumo()?.inativos ?? 0);
  readonly clientesPendentes = computed(() => this._resumo()?.pendentes ?? 0);

  async carregarClientes(): Promise<void> {
    this._isLoading.set(true);
    this._error.set(null);

    try {
      const response = await firstValueFrom(
        this.graphql.query<{ resumoClientes: ResumoClientesGQL }>(QUERIES.RESUMO_CLIENTES)
      );

      const resumo = response.resumoClientes;
      this._resumo.set({
        total: resumo.totalClientes,
        ativos: resumo.ativos,
        inativos: resumo.inativos,
        pendentes: resumo.pendentes
      });

      this._clientes.set(resumo.clientes.map(c => this.mapToCliente(c)));
    } catch (err) {
      console.error('Erro ao carregar clientes:', err);
      const message = err instanceof Error ? err.message : 'Erro ao carregar clientes';
      this._error.set(message);
      this.toastService.error(message);
    } finally {
      this._isLoading.set(false);
    }
  }

  async getById(id: string): Promise<Cliente | undefined> {
    try {
      const response = await firstValueFrom(
        this.graphql.query<{ clienteById: ClienteGQL | null }>(QUERIES.CLIENTE_BY_ID, { id })
      );

      return response.clienteById ? this.mapToCliente(response.clienteById) : undefined;
    } catch (err) {
      console.error('Erro ao buscar cliente:', err);
      return undefined;
    }
  }

  async create(data: ClienteFormData): Promise<Cliente | null> {
    this._isLoading.set(true);

    try {
      const input = this.mapToInput(data);
      const response = await firstValueFrom(
        this.graphql.mutate<{ criarCliente: ClienteGQL }>(MUTATIONS.CRIAR_CLIENTE, { input })
      );

      this.toastService.success('Cliente criado com sucesso!');
      await this.carregarClientes();

      return this.mapToCliente(response.criarCliente);
    } catch (err) {
      console.error('Erro ao criar cliente:', err);
      const message = err instanceof Error ? err.message : 'Erro ao criar cliente';
      this.toastService.error(message);
      return null;
    } finally {
      this._isLoading.set(false);
    }
  }

  async update(id: string, data: ClienteFormData): Promise<Cliente | null> {
    this._isLoading.set(true);

    try {
      const input = { id, ...this.mapToInput(data) };
      const response = await firstValueFrom(
        this.graphql.mutate<{ atualizarCliente: ClienteGQL }>(MUTATIONS.ATUALIZAR_CLIENTE, { input })
      );

      this.toastService.success('Cliente atualizado com sucesso!');
      await this.carregarClientes();

      return this.mapToCliente(response.atualizarCliente);
    } catch (err) {
      console.error('Erro ao atualizar cliente:', err);
      const message = err instanceof Error ? err.message : 'Erro ao atualizar cliente';
      this.toastService.error(message);
      return null;
    } finally {
      this._isLoading.set(false);
    }
  }

  async updateStatus(id: string, status: ClienteStatus): Promise<boolean> {
    this._isLoading.set(true);

    try {
      await firstValueFrom(
        this.graphql.mutate<{ alterarStatusCliente: ClienteGQL }>(MUTATIONS.ALTERAR_STATUS, { id, status })
      );

      this.toastService.success(`Status alterado para ${status}`);
      await this.carregarClientes();

      return true;
    } catch (err) {
      console.error('Erro ao alterar status:', err);
      const message = err instanceof Error ? err.message : 'Erro ao alterar status';
      this.toastService.error(message);
      return false;
    } finally {
      this._isLoading.set(false);
    }
  }

  async delete(id: string): Promise<boolean> {
    this._isLoading.set(true);

    try {
      await firstValueFrom(
        this.graphql.mutate<{ excluirCliente: boolean }>(MUTATIONS.EXCLUIR_CLIENTE, { id })
      );

      this.toastService.success('Cliente excluido com sucesso!');
      await this.carregarClientes();

      return true;
    } catch (err) {
      console.error('Erro ao excluir cliente:', err);
      const message = err instanceof Error ? err.message : 'Erro ao excluir cliente';
      this.toastService.error(message);
      return false;
    } finally {
      this._isLoading.set(false);
    }
  }

  private mapToCliente(gql: ClienteGQL): Cliente {
    return {
      id: gql.id,
      nome: gql.nome,
      sobrenome: gql.sobrenome,
      dataNascimento: gql.dataNascimento ?? '',
      status: gql.status,
      observacoes: gql.observacoes ?? undefined,
      criadoEm: gql.criadoEm ?? '',
      atualizadoEm: gql.atualizadoEm ?? '',
      documentos: (gql.documentos ?? []).map(d => ({
        tipo: d.tipo as Documento['tipo'],
        numero: d.numero,
        principal: d.principal,
        arquivoUrl: d.arquivoUrl ?? undefined,
        nomeArquivo: d.nomeArquivo ?? undefined
      })),
      enderecos: (gql.enderecos ?? []).map(e => ({
        tipo: e.tipo as Endereco['tipo'],
        cep: e.cep,
        logradouro: e.logradouro,
        numero: e.numero,
        complemento: e.complemento ?? undefined,
        bairro: e.bairro,
        cidade: e.cidade,
        estado: e.estado,
        principal: e.principal
      })),
      contatos: (gql.contatos ?? []).map(c => ({
        tipo: c.tipo as Contato['tipo'],
        valor: c.valor,
        principal: c.principal
      }))
    };
  }

  private mapToInput(data: ClienteFormData) {
    return {
      nome: data.nome,
      sobrenome: data.sobrenome,
      dataNascimento: data.dataNascimento || null,
      observacoes: data.observacoes || null,
      documentos: data.documentos.map(d => ({
        tipo: d.tipo,
        numero: d.numero,
        principal: d.principal,
        arquivoUrl: d.arquivoUrl || null,
        nomeArquivo: d.nomeArquivo || null
      })),
      enderecos: data.enderecos.map(e => ({
        tipo: e.tipo,
        cep: e.cep,
        logradouro: e.logradouro,
        numero: e.numero,
        complemento: e.complemento || null,
        bairro: e.bairro,
        cidade: e.cidade,
        estado: e.estado,
        principal: e.principal
      })),
      contatos: data.contatos.map(c => ({
        tipo: c.tipo,
        valor: c.valor,
        principal: c.principal
      }))
    };
  }
}
