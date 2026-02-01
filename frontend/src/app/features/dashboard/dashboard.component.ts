import { Component, signal, inject, OnInit, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MilhasApiService } from '../milhas/services/milhas-api.service';

interface Stat {
  label: string;
  value: string | number;
  icon: string;
  trend: string;
  color: 'gold' | 'blue' | 'green' | 'purple';
}

interface QuickAction {
  label: string;
  icon: string;
  route: string;
  color: 'primary' | 'success' | 'info' | 'warning';
}

interface Activity {
  id: number;
  text: string;
  time: string;
  icon: string;
  type: 'success' | 'info' | 'warning';
}

interface Trip {
  id: number;
  origin: string;
  destination: string;
  date: string;
  client: string;
}

interface ChartBar {
  label: string;
  value: number;
  active: boolean;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  private readonly milhasApi = inject(MilhasApiService);

  readonly selectedPeriod = signal<'week' | 'month' | 'year'>('week');
  readonly isLoading = this.milhasApi.isLoading;

  // Stats dinamicos baseados nos dados do backend
  readonly stats = computed((): Stat[] => {
    const resumo = this.milhasApi.resumo();
    const totalMilhas = resumo?.totalGeral ?? 0;
    const totalContas = resumo?.quantidadeContas ?? 0;
    const totalOwners = resumo?.porOwner.length ?? 0;

    return [
      { label: 'Titulares', value: totalOwners, icon: 'groups', trend: '', color: 'blue' },
      { label: 'Contas de Milhas', value: totalContas, icon: 'flight', trend: '', color: 'gold' },
      { label: 'Milhas Totais', value: this.formatMilhas(totalMilhas), icon: 'loyalty', trend: '', color: 'green' },
      { label: 'Custo Total', value: this.formatCurrency(this.getTotalCusto()), icon: 'payments', trend: '', color: 'purple' }
    ];
  });

  // Milhas por owner para grafico
  readonly milhasPorOwner = computed(() => {
    const porOwner = this.milhasApi.porOwner();
    return porOwner.map(o => ({
      label: o.owner,
      value: o.total,
      active: false
    }));
  });

  // Milhas por programa para tabela
  readonly milhasPorPrograma = computed(() => this.milhasApi.porPrograma());

  // Contas de milhas
  readonly contasMilhas = computed(() => this.milhasApi.contas());

  readonly quickActions: QuickAction[] = [
    { label: 'Novo Cliente', icon: 'person_add', route: '/app/clientes/novo', color: 'primary' },
    { label: 'Nova Viagem', icon: 'flight', route: '/app/viagens/nova', color: 'success' },
    { label: 'Registrar Milhas', icon: 'loyalty', route: '/app/milhas/registrar', color: 'info' },
    { label: 'Novo Programa', icon: 'add_circle', route: '/app/programas/incluir', color: 'warning' }
  ];

  readonly recentActivities: Activity[] = [
    { id: 1, text: 'Novo cliente cadastrado: Maria Silva', time: 'Ha 5 minutos', icon: 'person_add', type: 'success' },
    { id: 2, text: 'Viagem confirmada: GRU → JFK', time: 'Ha 15 minutos', icon: 'flight', type: 'info' },
    { id: 3, text: 'Milhas adicionadas: Joao Pedro', time: 'Ha 30 minutos', icon: 'loyalty', type: 'success' },
    { id: 4, text: 'Pagamento recebido: R$ 3.450,00', time: 'Ha 1 hora', icon: 'payments', type: 'success' },
    { id: 5, text: 'Cliente atualizado: Ana Costa', time: 'Ha 2 horas', icon: 'edit', type: 'info' }
  ];

  readonly upcomingTrips: Trip[] = [
    { id: 1, origin: 'GRU', destination: 'MIA', date: '15 Fev', client: 'Carlos Santos' },
    { id: 2, origin: 'CGH', destination: 'SDU', date: '18 Fev', client: 'Fernanda Lima' },
    { id: 3, origin: 'BSB', destination: 'LIS', date: '22 Fev', client: 'Roberto Almeida' }
  ];

  chartData: ChartBar[] = [];

  ngOnInit(): void {
    this.loadData();
  }

  async loadData(): Promise<void> {
    await this.milhasApi.carregarResumo();
    this.updateChartData();
  }

  setPeriod(period: 'week' | 'month' | 'year'): void {
    this.selectedPeriod.set(period);
  }

  refreshData(): void {
    this.loadData();
  }

  private updateChartData(): void {
    // Usar dados reais de milhas por owner
    const porOwner = this.milhasApi.porOwner();
    if (porOwner.length > 0) {
      const maxMilhas = Math.max(...porOwner.map(o => o.total));
      this.chartData = porOwner.map((o, i) => ({
        label: o.owner,
        value: Math.round((o.total / maxMilhas) * 100),
        active: i === 0
      }));
    }
  }

  private getTotalCusto(): number {
    const contas = this.milhasApi.contas();
    return contas.reduce((sum, c) => sum + c.custoBaseTotalBRL, 0);
  }

  formatMilhas(value: number): string {
    if (value >= 1000000) {
      return `${(value / 1000000).toFixed(1)}M`;
    }
    if (value >= 1000) {
      return `${(value / 1000).toFixed(0)}K`;
    }
    return value.toLocaleString('pt-BR');
  }

  private formatCurrency(value: number): string {
    if (value >= 1000) {
      return `R$ ${(value / 1000).toFixed(1)}K`;
    }
    return value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }

  getIcon(iconName: string): string {
    const icons: Record<string, string> = {
      // Dashboard stats
      groups: 'M12 12.75c1.63 0 3.07.39 4.24.9 1.08.48 1.76 1.56 1.76 2.73V18H6v-1.61c0-1.18.68-2.26 1.76-2.73 1.17-.52 2.61-.91 4.24-.91zM4 13c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm1.13 1.1c-.37-.06-.74-.1-1.13-.1-.99 0-1.93.21-2.78.58C.48 14.9 0 15.62 0 16.43V18h4.5v-1.61c0-.83.23-1.61.63-2.29zM20 13c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm4 3.43c0-.81-.48-1.53-1.22-1.85-.85-.37-1.79-.58-2.78-.58-.39 0-.76.04-1.13.1.4.68.63 1.46.63 2.29V18H24v-1.57zM12 6c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3z',
      flight: 'M21 16v-2l-8-5V3.5c0-.83-.67-1.5-1.5-1.5S10 2.67 10 3.5V9l-8 5v2l8-2.5V19l-2 1.5V22l3.5-1 3.5 1v-1.5L13 19v-5.5l8 2.5z',
      loyalty: 'M21.41 11.58l-9-9C12.05 2.22 11.55 2 11 2H4c-1.1 0-2 .9-2 2v7c0 .55.22 1.05.59 1.42l9 9c.36.36.86.58 1.41.58.55 0 1.05-.22 1.41-.59l7-7c.37-.36.59-.86.59-1.41 0-.55-.23-1.06-.59-1.42zM5.5 7C4.67 7 4 6.33 4 5.5S4.67 4 5.5 4 7 4.67 7 5.5 6.33 7 5.5 7z',
      payments: 'M19 14V6c0-1.1-.9-2-2-2H3c-1.1 0-2 .9-2 2v8c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2zm-9-1c-1.66 0-3-1.34-3-3s1.34-3 3-3 3 1.34 3 3-1.34 3-3 3zm13-6v11c0 1.1-.9 2-2 2H4v-2h17V7h2z',
      // Quick actions
      person_add: 'M15 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm-9-2V7H4v3H1v2h3v3h2v-3h3v-2H6zm9 4c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z',
      add_circle: 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm5 11h-4v4h-2v-4H7v-2h4V7h2v4h4v2z',
      // Section icons
      edit: 'M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z'
    };
    return icons[iconName] || icons['groups'];
  }
}
