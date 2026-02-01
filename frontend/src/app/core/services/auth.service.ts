import { Injectable, signal, computed, inject } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { DevDataService, DevAccount, DevUser as DevUserData } from './dev-data.service';
import { firstValueFrom } from 'rxjs';

export interface User {
  id: string;
  nome: string;
  email: string;
  tenantId: string;
  tenantNome: string;
  roles: string[];
}

export interface AuthToken {
  accessToken: string;
  refreshToken: string;
  expiresAt: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly TOKEN_KEY = 'vv_auth_token';
  private readonly USER_KEY = 'vv_user';

  private readonly _user = signal<User | null>(null);
  private readonly _isAuthenticated = signal(false);
  private readonly _devDataLoaded = signal(false);

  private readonly router = inject(Router);
  private readonly devDataService = inject(DevDataService);

  readonly user = this._user.asReadonly();
  readonly isAuthenticated = this._isAuthenticated.asReadonly();
  readonly tenantId = computed(() => this._user()?.tenantId ?? null);
  readonly tenantNome = computed(() => this._user()?.tenantNome ?? null);
  readonly authEnabled = environment.authEnabled;

  // Cache de dados de dev carregados do banco
  private devAccounts: DevAccount[] = [];
  private devUsersByAccount = new Map<string, DevUserData[]>();

  constructor() {
    if (!this.authEnabled) {
      // DEV: auto-login com usuario admin do banco de dados
      this.autoLoginDev();
    } else {
      this.loadStoredSession();
    }
  }

  private async autoLoginDev(): Promise<void> {
    try {
      // Carregar accounts do banco de dados
      this.devAccounts = await firstValueFrom(this.devDataService.loadAccounts());

      if (this.devAccounts.length === 0) {
        console.warn('[AuthService] Nenhum account encontrado no banco. Usando fallback.');
        this.setFallbackDevSession();
        return;
      }

      // Usar o primeiro account
      const firstAccount = this.devAccounts[0];

      // Carregar usuarios desse account
      const users = await firstValueFrom(this.devDataService.loadUsersByAccount(firstAccount.id));
      this.devUsersByAccount.set(firstAccount.id, users);

      if (users.length === 0) {
        console.warn('[AuthService] Nenhum usuario encontrado para account ' + firstAccount.id);
        this.setFallbackDevSession();
        return;
      }

      // Usar o primeiro usuario (geralmente ROOT ou ADMIN)
      const firstUser = users.find(u => u.roles.includes('ROOT') || u.roles.includes('ADMIN')) || users[0];

      const devUser: User = {
        id: firstUser.id,
        nome: firstUser.nome,
        email: firstUser.email,
        tenantId: firstAccount.id,
        tenantNome: firstAccount.nome,
        roles: firstUser.roles
      };

      const devToken: AuthToken = {
        accessToken: 'dev-token-mock',
        refreshToken: 'dev-refresh-mock',
        expiresAt: Date.now() + (24 * 60 * 60 * 1000) // 24 horas
      };

      this._devDataLoaded.set(true);
      this.setSession(devUser, devToken);
      console.log('[AuthService] DEV auto-login: ' + devUser.email + ' @ ' + devUser.tenantNome);
    } catch (error) {
      console.error('[AuthService] Erro ao carregar dados de dev do banco:', error);
      this.setFallbackDevSession();
    }
  }

  private setFallbackDevSession(): void {
    const devUser: User = {
      id: 'dev-user-fallback',
      nome: 'Dev Admin (Fallback)',
      email: 'admin@dev.local',
      tenantId: '00000000-0000-0000-0000-000000000001',
      tenantNome: 'Dev Tenant',
      roles: ['ADMIN']
    };

    const devToken: AuthToken = {
      accessToken: 'dev-token-fallback',
      refreshToken: 'dev-refresh-fallback',
      expiresAt: Date.now() + (24 * 60 * 60 * 1000)
    };

    this.setSession(devUser, devToken);
  }

  private loadStoredSession(): void {
    try {
      const tokenData = sessionStorage.getItem(this.TOKEN_KEY);
      const userData = sessionStorage.getItem(this.USER_KEY);

      if (tokenData && userData) {
        const token: AuthToken = JSON.parse(tokenData);
        const user: User = JSON.parse(userData);

        // Verificar se token expirou
        if (token.expiresAt > Date.now()) {
          this._user.set(user);
          this._isAuthenticated.set(true);
        } else {
          this.clearSession();
        }
      }
    } catch {
      this.clearSession();
    }
  }

  async login(email: string, password: string): Promise<boolean> {
    if (this.authEnabled) {
      // HML/PROD: Implementar chamada real ao Keycloak/Cognito
      // TODO: Implementar OAuth2 flow
      console.warn('[AuthService] Auth enabled - OAuth2 flow not yet implemented');
      return false;
    }

    // DEV: Validar contra usuarios do banco de dados
    try {
      const emailLower = email.toLowerCase();

      // Garantir que accounts estao carregados
      if (this.devAccounts.length === 0) {
        this.devAccounts = await firstValueFrom(this.devDataService.loadAccounts());
      }

      // Buscar usuario em todos os accounts
      for (const account of this.devAccounts) {
        let users = this.devUsersByAccount.get(account.id);
        if (!users) {
          users = await firstValueFrom(this.devDataService.loadUsersByAccount(account.id));
          this.devUsersByAccount.set(account.id, users);
        }

        const foundUser = users.find(u => u.email.toLowerCase() === emailLower);
        if (foundUser) {
          // DEV: Validar senha com padrao simples (Role@123 ou senha123)
          const validPasswords = this.generateValidPasswords(foundUser.roles);
          if (!validPasswords.includes(password)) {
            console.warn('[AuthService] Senha invalida para ' + emailLower);
            return false;
          }

          const user: User = {
            id: foundUser.id,
            nome: foundUser.nome,
            email: foundUser.email,
            tenantId: account.id,
            tenantNome: account.nome,
            roles: foundUser.roles
          };

          const token: AuthToken = {
            accessToken: this.generateMockToken(),
            refreshToken: this.generateMockToken(),
            expiresAt: Date.now() + (8 * 60 * 60 * 1000) // 8 horas
          };

          this.setSession(user, token);
          console.log('[AuthService] DEV login: ' + user.email + ' @ ' + user.tenantNome);
          return true;
        }
      }

      console.warn('[AuthService] Usuario nao encontrado no banco: ' + emailLower);
      return false;
    } catch (error) {
      console.error('[AuthService] Erro no login:', error);
      return false;
    }
  }

  /**
   * Gera senhas validas para DEV baseado nas roles do usuario.
   * Aceita: Role@123 (ex: Admin@123), senha123, ou dev123
   */
  private generateValidPasswords(roles: string[]): string[] {
    const passwords = ['senha123', 'dev123'];
    for (const role of roles) {
      const capitalized = role.charAt(0).toUpperCase() + role.slice(1).toLowerCase();
      passwords.push(capitalized + '@123');
    }
    return passwords;
  }

  logout(): void {
    this.clearSession();
    this.router.navigate(['/landing']);
  }

  private setSession(user: User, token: AuthToken): void {
    sessionStorage.setItem(this.TOKEN_KEY, JSON.stringify(token));
    sessionStorage.setItem(this.USER_KEY, JSON.stringify(user));
    this._user.set(user);
    this._isAuthenticated.set(true);
  }

  private clearSession(): void {
    sessionStorage.removeItem(this.TOKEN_KEY);
    sessionStorage.removeItem(this.USER_KEY);
    this._user.set(null);
    this._isAuthenticated.set(false);
  }

  getAccessToken(): string | null {
    try {
      const tokenData = sessionStorage.getItem(this.TOKEN_KEY);
      if (tokenData) {
        const token: AuthToken = JSON.parse(tokenData);
        if (token.expiresAt > Date.now()) {
          return token.accessToken;
        }
      }
    } catch {
      // Token invalido
    }
    return null;
  }

  hasRole(role: string): boolean {
    return this._user()?.roles.includes(role) ?? false;
  }

  hasAnyRole(roles: string[]): boolean {
    const userRoles = this._user()?.roles ?? [];
    return roles.some(role => userRoles.includes(role));
  }

  /**
   * DEV ONLY: Troca o perfil do usuario para testar diferentes roles
   */
  switchDevProfile(email: string, nome: string, roles: string[]): void {
    if (this.authEnabled) {
      return; // Nao permite em HML/PROD
    }

    const currentUser = this._user();
    const devUser: User = {
      id: `dev-user-${roles[0].toLowerCase()}`,
      nome,
      email,
      tenantId: currentUser?.tenantId ?? '00000000-0000-0000-0000-000000000001',
      tenantNome: currentUser?.tenantNome ?? 'Vanessa Viagem',
      roles
    };

    const devToken: AuthToken = {
      accessToken: `dev-token-${roles[0].toLowerCase()}`,
      refreshToken: 'dev-refresh-mock',
      expiresAt: Date.now() + (24 * 60 * 60 * 1000)
    };

    this.setSession(devUser, devToken);
  }

  /**
   * DEV ONLY: Troca a account/tenant para testar isolamento multi-tenant
   */
  switchDevTenant(tenantId: string, tenantNome: string): void {
    if (this.authEnabled) {
      return; // Nao permite em HML/PROD
    }

    const currentUser = this._user();
    if (!currentUser) return;

    const devUser: User = {
      ...currentUser,
      tenantId,
      tenantNome
    };

    const devToken: AuthToken = {
      accessToken: `dev-token-tenant-${tenantId}`,
      refreshToken: 'dev-refresh-mock',
      expiresAt: Date.now() + (24 * 60 * 60 * 1000)
    };

    this.setSession(devUser, devToken);
  }

  private generateMockToken(): string {
    const array = new Uint8Array(32);
    crypto.getRandomValues(array);
    return Array.from(array, byte => byte.toString(16).padStart(2, '0')).join('');
  }
}
