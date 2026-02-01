import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { DevDataService, DevAccount, DevUser } from '../../services/dev-data.service';
import { environment } from '../../../../environments/environment';

interface DevProfile {
  email: string;
  nome: string;
  roles: string[];
  color: string;
}

interface DevAccountUI extends DevAccount {
  color: string;
  description: string;
}

type PanelMode = 'closed' | 'account' | 'user';

@Component({
  selector: 'app-dev-toolbar',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (isDevMode) {
      <!-- Banner fixo no topo -->
      <div class="dev-banner">
        <span class="dev-banner__badge">DEV</span>

        <!-- Account Section -->
        <div class="dev-banner__section">
          <span class="dev-banner__label">Account:</span>
          <button class="dev-banner__account" (click)="openPanel('account')">
            <span class="dev-banner__account-name">{{ currentUser()?.tenantNome }}</span>
          </button>
        </div>

        <span class="dev-banner__separator">|</span>

        <!-- User Section -->
        <div class="dev-banner__section">
          <span class="dev-banner__label">User:</span>
          <span class="dev-banner__user">{{ currentUser()?.nome }}</span>
          <span class="dev-banner__role" [style.background]="getCurrentUserColor()">
            {{ getRolesDisplay(currentUser()?.roles ?? []) }}
          </span>
        </div>

        <button class="dev-banner__btn" (click)="openPanel('user')">
          Trocar Perfil
        </button>
      </div>

      <!-- Painel de selecao -->
      @if (panelMode() !== 'closed') {
        <div class="dev-panel-overlay" (click)="closePanel()"></div>
        <div class="dev-panel">
          <div class="dev-panel__header">
            <h3>{{ panelMode() === 'account' ? 'Selecionar Account (Tenant)' : 'Selecionar Perfil de Teste' }}</h3>
            <button class="dev-panel__close" (click)="closePanel()">X</button>
          </div>

          <!-- Loading State -->
          @if (loading()) {
            <div class="dev-panel__loading">Carregando...</div>
          }

          <!-- Account Selection -->
          @if (panelMode() === 'account' && !loading()) {
            <div class="dev-panel__profiles">
              @for (account of accounts(); track account.id) {
                <button
                  class="dev-panel__profile"
                  [class.dev-panel__profile--active]="isAccountActive(account)"
                  [style.border-color]="account.color"
                  (click)="switchAccount(account)">
                  <span class="dev-panel__profile-badge" [style.background]="account.color">
                    {{ getAccountInitials(account.nome) }}
                  </span>
                  <div class="dev-panel__profile-info">
                    <span class="dev-panel__profile-name">{{ account.nome }}</span>
                    <span class="dev-panel__profile-desc">{{ account.description }}</span>
                    <span class="dev-panel__profile-id">ID: {{ account.id }}</span>
                  </div>
                  @if (isAccountActive(account)) {
                    <span class="dev-panel__profile-check">ATIVO</span>
                  }
                </button>
              }
            </div>
          }

          <!-- User Selection -->
          @if (panelMode() === 'user' && !loading()) {
            <div class="dev-panel__profiles">
              @for (profile of profiles(); track profile.email) {
                <button
                  class="dev-panel__profile"
                  [class.dev-panel__profile--active]="isProfileActive(profile)"
                  [style.border-color]="profile.color"
                  (click)="switchProfile(profile)">
                  <span class="dev-panel__profile-badge" [style.background]="profile.color">
                    {{ getRolesDisplay(profile.roles) }}
                  </span>
                  <div class="dev-panel__profile-info">
                    <span class="dev-panel__profile-name">{{ profile.nome }}</span>
                    <span class="dev-panel__profile-desc">{{ getDescription(getDescriptionKey(profile.roles)) }}</span>
                  </div>
                  @if (isProfileActive(profile)) {
                    <span class="dev-panel__profile-check">ATIVO</span>
                  }
                </button>
              }
            </div>
          }
        </div>
      }
    }
  `,
  styles: [`
    .dev-banner {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      height: 4rem;
      background: linear-gradient(90deg, #dc2626 0%, #991b1b 100%);
      display: flex;
      align-items: center;
      padding: 0 2rem;
      gap: 1.2rem;
      z-index: 10000;
      font-family: 'Montserrat', sans-serif;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
    }

    .dev-banner__badge {
      background: #fff;
      color: #dc2626;
      padding: 0.4rem 1rem;
      border-radius: 0.4rem;
      font-weight: 800;
      font-size: 1.2rem;
      letter-spacing: 0.1rem;
    }

    .dev-banner__section {
      display: flex;
      align-items: center;
      gap: 0.8rem;
    }

    .dev-banner__label {
      color: #fca5a5;
      font-size: 1.1rem;
      font-weight: 500;
    }

    .dev-banner__account {
      background: rgba(255, 255, 255, 0.1);
      border: 2px solid white;
      color: white;
      padding: 0.4rem 1.2rem;
      border-radius: 0.4rem;
      font-weight: 600;
      font-size: 1.2rem;
      cursor: pointer;
      transition: all 0.2s;

      &:hover {
        background: rgba(255, 255, 255, 0.2);
        transform: scale(1.02);
      }
    }

    .dev-banner__account-name {
      color: white;
    }

    .dev-banner__separator {
      color: #fca5a5;
      font-size: 1.4rem;
      opacity: 0.5;
    }

    .dev-banner__user {
      color: #fecaca;
      font-size: 1.3rem;
    }

    .dev-banner__role {
      color: white;
      padding: 0.4rem 1rem;
      border-radius: 0.4rem;
      font-weight: 700;
      font-size: 1.1rem;
    }

    .dev-banner__btn {
      margin-left: auto;
      background: white;
      color: #dc2626;
      border: none;
      padding: 0.8rem 1.6rem;
      border-radius: 0.4rem;
      font-weight: 600;
      font-size: 1.2rem;
      cursor: pointer;
      transition: all 0.2s;

      &:hover {
        background: #fef2f2;
        transform: scale(1.05);
      }
    }

    .dev-panel-overlay {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.5);
      z-index: 10001;
    }

    .dev-panel {
      position: fixed;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      background: #1e293b;
      border-radius: 1.2rem;
      padding: 2rem;
      min-width: 50rem;
      max-width: 90vw;
      max-height: 80vh;
      overflow-y: auto;
      z-index: 10002;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
      font-family: 'Montserrat', sans-serif;
    }

    .dev-panel__header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
      padding-bottom: 1.2rem;
      border-bottom: 1px solid #334155;

      h3 {
        color: #f1f5f9;
        font-size: 1.8rem;
        font-weight: 600;
        margin: 0;
      }
    }

    .dev-panel__close {
      background: #475569;
      border: none;
      color: white;
      width: 3.2rem;
      height: 3.2rem;
      border-radius: 0.4rem;
      font-size: 1.4rem;
      font-weight: 700;
      cursor: pointer;

      &:hover {
        background: #64748b;
      }
    }

    .dev-panel__loading {
      color: #94a3b8;
      font-size: 1.4rem;
      text-align: center;
      padding: 2rem;
    }

    .dev-panel__profiles {
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }

    .dev-panel__profile {
      display: flex;
      align-items: center;
      gap: 1.2rem;
      padding: 1.2rem 1.6rem;
      background: #334155;
      border: 2px solid transparent;
      border-radius: 0.8rem;
      cursor: pointer;
      transition: all 0.2s;
      text-align: left;

      &:hover {
        background: #475569;
        transform: translateX(4px);
      }

      &--active {
        background: #1e3a5f;
        border-color: currentColor;
      }
    }

    .dev-panel__profile-badge {
      color: white;
      padding: 0.6rem 1rem;
      border-radius: 0.4rem;
      font-weight: 700;
      font-size: 1.1rem;
      min-width: 8rem;
      text-align: center;
    }

    .dev-panel__profile-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 0.4rem;
    }

    .dev-panel__profile-name {
      color: #f1f5f9;
      font-weight: 600;
      font-size: 1.4rem;
    }

    .dev-panel__profile-desc {
      color: #94a3b8;
      font-size: 1.2rem;
    }

    .dev-panel__profile-id {
      color: #64748b;
      font-size: 1rem;
      font-family: monospace;
    }

    .dev-panel__profile-check {
      background: #22c55e;
      color: white;
      padding: 0.4rem 0.8rem;
      border-radius: 0.4rem;
      font-size: 1rem;
      font-weight: 700;
    }
  `]
})
export class DevToolbarComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly devDataService = inject(DevDataService);

  readonly isDevMode = !environment.authEnabled;
  readonly panelMode = signal<PanelMode>('closed');
  readonly currentUser = this.authService.user;
  readonly loading = signal(false);

  readonly accounts = signal<DevAccountUI[]>([]);
  readonly profiles = signal<DevProfile[]>([]);

  readonly descriptions: Record<string, string> = {
    'ADMIN,ROOT': 'Todas as permissoes (ROOT + ADMIN)',
    ROOT: 'Todas as permissoes, sem restricoes',
    ADMIN: 'Gerencia usuarios, aprova solicitacoes',
    MANAGER: 'Insert/Update, requer aprovacao para delete',
    OPERATOR: 'Apenas Insert, requer aprovacao',
    VIEWER: 'Apenas visualizacao, sem edicao'
  };

  private readonly colors = ['#2563eb', '#7c3aed', '#0891b2', '#64748b', '#dc2626', '#ca8a04'];
  private readonly roleColors: Record<string, string> = {
    ROOT: '#7c3aed',
    ADMIN: '#2563eb',
    MANAGER: '#0891b2',
    OPERATOR: '#ca8a04',
    VIEWER: '#64748b'
  };

  ngOnInit(): void {
    if (this.isDevMode) {
      this.loadAccountsFromBackend();
    }
  }

  private loadAccountsFromBackend(): void {
    this.devDataService.loadAccounts().subscribe(accounts => {
      const accountsWithUI = accounts.map((account, index) => ({
        ...account,
        color: this.colors[index % this.colors.length],
        description: this.getAccountDescription(account)
      }));
      this.accounts.set(accountsWithUI);
    });
  }

  private loadUsersFromBackend(accountId: string): void {
    this.loading.set(true);
    this.devDataService.loadUsersByAccount(accountId).subscribe(users => {
      const profilesWithUI = users.map(user => ({
        email: user.email,
        nome: user.nome,
        roles: user.roles,
        color: this.roleColors[user.roles[0]] || '#64748b'
      }));
      this.profiles.set(profilesWithUI);
      this.loading.set(false);
    });
  }

  private getAccountDescription(account: DevAccount): string {
    const planDesc: Record<string, string> = {
      PREMIUM: 'Plano Premium - Dados de producao simulados',
      BUSINESS: 'Plano Business - Dados de teste isolados',
      STARTER: 'Plano Starter - Dados minimos para QA',
      FREE: 'Plano Free - Sem dados para teste de onboarding'
    };
    return planDesc[account.plan] || `Plano ${account.plan}`;
  }

  openPanel(mode: 'account' | 'user'): void {
    this.panelMode.set(mode);
    if (mode === 'user') {
      const currentTenantId = this.currentUser()?.tenantId;
      if (currentTenantId) {
        this.loadUsersFromBackend(currentTenantId);
      }
    }
  }

  closePanel(): void {
    this.panelMode.set('closed');
  }

  isAccountActive(account: DevAccountUI): boolean {
    return this.currentUser()?.tenantId === account.id;
  }

  isProfileActive(profile: DevProfile): boolean {
    const user = this.currentUser();
    if (!user?.roles) return false;
    const userRoles = [...user.roles].sort().join(',');
    const profileRoles = [...profile.roles].sort().join(',');
    return userRoles === profileRoles;
  }

  getCurrentAccountColor(): string {
    const tenantId = this.currentUser()?.tenantId;
    return this.accounts().find(a => a.id === tenantId)?.color ?? '#64748b';
  }

  getCurrentUserColor(): string {
    const userRoles = this.currentUser()?.roles;
    if (!userRoles || userRoles.length === 0) return '#64748b';
    return this.roleColors[userRoles[0]] || '#64748b';
  }

  getDescription(role: string): string {
    return this.descriptions[role] ?? '';
  }

  getRolesDisplay(roles: string[]): string {
    if (!roles || roles.length === 0) return '';
    if (roles.length > 1) {
      return 'DEV ADMIN';
    }
    return roles[0];
  }

  getDescriptionKey(roles: string[]): string {
    return [...roles].sort().join(',');
  }

  getAccountInitials(nome: string): string {
    return nome.split(' ').map(w => w[0]).join('').substring(0, 2).toUpperCase();
  }

  switchAccount(account: DevAccountUI): void {
    this.authService.switchDevTenant(account.id, account.nome);
    this.closePanel();
  }

  switchProfile(profile: DevProfile): void {
    this.authService.switchDevProfile(profile.email, profile.nome, profile.roles);
    this.closePanel();
  }
}
