import { Injectable, inject, signal, computed } from '@angular/core';
import { GraphQLService } from '../../../core/services/graphql.service';
import { ToastService } from '../../../shared/services/toast.service';
import { firstValueFrom } from 'rxjs';

// Types
export interface ContaPrograma {
  id: string;
  programaId: string;
  programaNome: string;
  owner: string;
  saldoMilhas: number;
  custoBaseTotalBRL: number;
  custoMedioMilheiroAtual: number;
}

export interface Transacao {
  id: string;
  contaProgramaId: string;
  tipo: 'COMPRA' | 'VENDA' | 'BONUS';
  milhas: number;
  valorBRL: number;
  fonte: string | null;
  observacao: string | null;
}

export interface TotalPorOwner {
  owner: string;
  total: number;
}

export interface TotalPorPrograma {
  programa: string;
  total: number;
}

export interface ResumoContas {
  totalGeral: number;
  quantidadeContas: number;
  porOwner: TotalPorOwner[];
  porPrograma: TotalPorPrograma[];
  contas: ContaPrograma[];
}

export interface ResultadoCompra {
  transacao: Transacao;
  contaAtualizada: ContaPrograma;
}

export interface ResultadoVenda {
  transacao: Transacao;
  contaAtualizada: ContaPrograma;
  custoRemovido: number;
  lucro: number;
}

// GraphQL Queries
const QUERIES = {
  RESUMO_CONTAS: `
    query ResumoContas {
      resumoContas {
        totalGeral
        quantidadeContas
        porOwner {
          owner
          total
        }
        porPrograma {
          programa
          total
        }
        contas {
          id
          programaId
          programaNome
          owner
          saldoMilhas
          custoBaseTotalBRL
          custoMedioMilheiroAtual
        }
      }
    }
  `,

  CONTAS_PROGRAMA: `
    query ContasPrograma {
      contasPrograma {
        id
        programaId
        programaNome
        owner
        saldoMilhas
        custoBaseTotalBRL
        custoMedioMilheiroAtual
      }
    }
  `,

  CONTAS_POR_OWNER: `
    query ContasPorOwner($owner: String!) {
      contasProgramaPorOwner(owner: $owner) {
        id
        programaId
        programaNome
        owner
        saldoMilhas
        custoBaseTotalBRL
        custoMedioMilheiroAtual
      }
    }
  `,

  TRANSACOES: `
    query Transacoes($contaProgramaId: ID!) {
      transacoes(contaProgramaId: $contaProgramaId) {
        id
        contaProgramaId
        tipo
        milhas
        valorBRL
        fonte
        observacao
      }
    }
  `
};

// GraphQL Mutations
const MUTATIONS = {
  REGISTRAR_COMPRA: `
    mutation RegistrarCompra($input: RegistrarCompraInput!) {
      registrarCompra(input: $input) {
        transacao {
          id
          tipo
          milhas
          valorBRL
        }
        contaAtualizada {
          id
          saldoMilhas
          custoBaseTotalBRL
          custoMedioMilheiroAtual
        }
      }
    }
  `,

  REGISTRAR_BONUS: `
    mutation RegistrarBonus($input: RegistrarBonusInput!) {
      registrarBonus(input: $input) {
        transacao {
          id
          tipo
          milhas
          valorBRL
          fonte
        }
        contaAtualizada {
          id
          saldoMilhas
          custoBaseTotalBRL
          custoMedioMilheiroAtual
        }
      }
    }
  `,

  REGISTRAR_VENDA: `
    mutation RegistrarVenda($input: RegistrarVendaInput!) {
      registrarVenda(input: $input) {
        transacao {
          id
          tipo
          milhas
          valorBRL
        }
        contaAtualizada {
          id
          saldoMilhas
          custoBaseTotalBRL
          custoMedioMilheiroAtual
        }
        custoRemovido
        lucro
      }
    }
  `
};

@Injectable({
  providedIn: 'root'
})
export class MilhasApiService {
  private readonly graphql = inject(GraphQLService);
  private readonly toastService = inject(ToastService);

  // State
  private readonly _contas = signal<ContaPrograma[]>([]);
  private readonly _resumo = signal<ResumoContas | null>(null);
  private readonly _isLoading = signal(false);
  private readonly _error = signal<string | null>(null);

  // Public readonly signals
  readonly contas = this._contas.asReadonly();
  readonly resumo = this._resumo.asReadonly();
  readonly isLoading = this._isLoading.asReadonly();
  readonly error = this._error.asReadonly();

  // Computed
  readonly totalMilhas = computed(() => this._resumo()?.totalGeral ?? 0);
  readonly totalContas = computed(() => this._resumo()?.quantidadeContas ?? 0);
  readonly porOwner = computed(() => this._resumo()?.porOwner ?? []);
  readonly porPrograma = computed(() => this._resumo()?.porPrograma ?? []);

  // ==============================
  // QUERIES
  // ==============================

  async carregarResumo(): Promise<void> {
    this._isLoading.set(true);
    this._error.set(null);

    try {
      const response = await firstValueFrom(
        this.graphql.query<{ resumoContas: ResumoContas }>(QUERIES.RESUMO_CONTAS)
      );

      this._resumo.set(response.resumoContas);
      this._contas.set(response.resumoContas.contas);
    } catch (err) {
      console.error('Erro ao carregar resumo:', err);
      const message = err instanceof Error
        ? err.message
        : 'Backend indisponivel. Inicie o servidor: cd backend && ./gradlew bootRun';
      this._error.set(message);
      this.toastService.error(message);
    } finally {
      this._isLoading.set(false);
    }
  }

  async carregarContas(): Promise<ContaPrograma[]> {
    this._isLoading.set(true);

    try {
      const response = await firstValueFrom(
        this.graphql.query<{ contasPrograma: ContaPrograma[] }>(QUERIES.CONTAS_PROGRAMA)
      );

      this._contas.set(response.contasPrograma);
      return response.contasPrograma;
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Erro ao carregar contas';
      this.toastService.error(message);
      return [];
    } finally {
      this._isLoading.set(false);
    }
  }

  async carregarContasPorOwner(owner: string): Promise<ContaPrograma[]> {
    try {
      const response = await firstValueFrom(
        this.graphql.query<{ contasProgramaPorOwner: ContaPrograma[] }>(
          QUERIES.CONTAS_POR_OWNER,
          { owner }
        )
      );

      return response.contasProgramaPorOwner;
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Erro ao carregar contas';
      this.toastService.error(message);
      return [];
    }
  }

  async carregarTransacoes(contaProgramaId: string): Promise<Transacao[]> {
    try {
      const response = await firstValueFrom(
        this.graphql.query<{ transacoes: Transacao[] }>(
          QUERIES.TRANSACOES,
          { contaProgramaId }
        )
      );

      return response.transacoes;
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Erro ao carregar transacoes';
      this.toastService.error(message);
      return [];
    }
  }

  // ==============================
  // MUTATIONS
  // ==============================

  async registrarCompra(input: {
    programaId: string;
    programaNome: string;
    owner: string;
    milhas: number;
    valorBRL: number;
    data: string;
    observacao?: string;
  }): Promise<ResultadoCompra | null> {
    this._isLoading.set(true);

    try {
      const response = await firstValueFrom(
        this.graphql.mutate<{ registrarCompra: ResultadoCompra }>(
          MUTATIONS.REGISTRAR_COMPRA,
          { input }
        )
      );

      this.toastService.success('Compra registrada com sucesso!');
      await this.carregarResumo(); // Refresh data

      return response.registrarCompra;
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Erro ao registrar compra';
      this.toastService.error(message);
      return null;
    } finally {
      this._isLoading.set(false);
    }
  }

  async registrarBonus(input: {
    contaProgramaId: string;
    milhas: number;
    fonte: string;
    data: string;
    observacao?: string;
  }): Promise<ResultadoCompra | null> {
    this._isLoading.set(true);

    try {
      const response = await firstValueFrom(
        this.graphql.mutate<{ registrarBonus: ResultadoCompra }>(
          MUTATIONS.REGISTRAR_BONUS,
          { input }
        )
      );

      this.toastService.success('Bonus registrado com sucesso!');
      await this.carregarResumo();

      return response.registrarBonus;
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Erro ao registrar bonus';
      this.toastService.error(message);
      return null;
    } finally {
      this._isLoading.set(false);
    }
  }

  async registrarVenda(input: {
    contaProgramaId: string;
    milhas: number;
    valorBRL: number;
    data: string;
    observacao?: string;
  }): Promise<ResultadoVenda | null> {
    this._isLoading.set(true);

    try {
      const response = await firstValueFrom(
        this.graphql.mutate<{ registrarVenda: ResultadoVenda }>(
          MUTATIONS.REGISTRAR_VENDA,
          { input }
        )
      );

      const result = response.registrarVenda;
      this.toastService.success(
        `Venda registrada! Lucro: R$ ${result.lucro.toFixed(2)}`
      );
      await this.carregarResumo();

      return result;
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Erro ao registrar venda';
      this.toastService.error(message);
      return null;
    } finally {
      this._isLoading.set(false);
    }
  }

  // ==============================
  // HELPERS
  // ==============================

  getContaById(id: string): ContaPrograma | undefined {
    return this._contas().find(c => c.id === id);
  }

  getContasByOwner(owner: string): ContaPrograma[] {
    return this._contas().filter(c => c.owner === owner);
  }

  formatCurrency(value: number): string {
    return value.toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    });
  }

  formatMilhas(value: number): string {
    return value.toLocaleString('pt-BR');
  }
}
