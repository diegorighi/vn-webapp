import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, of, map, catchError } from 'rxjs';

export interface DevAccount {
  id: string;
  nome: string;
  slug: string;
  email: string;
  status: string;
  plan: string;
}

export interface DevUser {
  id: string;
  email: string;
  nome: string;
  roles: string[];
}

interface GraphQLResponse<T> {
  data: T;
  errors?: Array<{ message: string }>;
}

/**
 * Service to fetch development data from backend.
 * Used by DevToolbar to load accounts and users from the database
 * instead of using hardcoded values.
 *
 * Falls back to hardcoded data if backend is unavailable.
 */
@Injectable({
  providedIn: 'root'
})
export class DevDataService {
  private readonly http = inject(HttpClient);
  private readonly graphqlUrl = environment.graphqlUrl || '/graphql';

  /**
   * Default accounts used as fallback when backend is unavailable
   */
  private readonly fallbackAccounts: DevAccount[] = [
    { id: '00000000-0000-0000-0000-000000000001', nome: 'Vanessa Viagem', slug: 'vanessa-viagem', email: 'contato@vanessaviagem.com.br', status: 'ACTIVE', plan: 'PREMIUM' },
    { id: '00000000-0000-0000-0000-000000000002', nome: 'Milhas Express', slug: 'milhas-express', email: 'contato@milhasexpress.com.br', status: 'ACTIVE', plan: 'BUSINESS' },
    { id: '00000000-0000-0000-0000-000000000003', nome: 'Voando Alto Turismo', slug: 'voando-alto', email: 'contato@voandoalto.com.br', status: 'ACTIVE', plan: 'STARTER' },
    { id: '00000000-0000-0000-0000-000000000004', nome: 'Empty Account', slug: 'empty-account', email: 'empty@test.com', status: 'TRIAL', plan: 'FREE' }
  ];

  /**
   * Default users used as fallback when backend is unavailable
   */
  private readonly fallbackUsers: DevUser[] = [
    { id: '20000000-0000-0000-0000-000000000001', email: 'root@vanessaviagem.com.br', nome: 'Usuario Root', roles: ['ROOT'] },
    { id: '20000000-0000-0000-0000-000000000002', email: 'admin@vanessaviagem.com.br', nome: 'Usuario Admin', roles: ['ADMIN'] },
    { id: '20000000-0000-0000-0000-000000000003', email: 'manager@vanessaviagem.com.br', nome: 'Usuario Manager', roles: ['MANAGER'] },
    { id: '20000000-0000-0000-0000-000000000004', email: 'operator@vanessaviagem.com.br', nome: 'Usuario Operator', roles: ['OPERATOR'] },
    { id: '20000000-0000-0000-0000-000000000005', email: 'viewer@vanessaviagem.com.br', nome: 'Usuario Viewer', roles: ['VIEWER'] }
  ];

  /**
   * Fetches all accounts from the backend.
   * Falls back to hardcoded data if backend is unavailable.
   */
  loadAccounts(): Observable<DevAccount[]> {
    const query = `
      query {
        devAccounts {
          id
          nome
          slug
          email
          status
          plan
        }
      }
    `;

    return this.http.post<GraphQLResponse<{ devAccounts: DevAccount[] }>>(
      this.graphqlUrl,
      { query }
    ).pipe(
      map(response => {
        if (response.errors?.length) {
          console.warn('[DevDataService] GraphQL errors:', response.errors);
          return this.fallbackAccounts;
        }
        return response.data.devAccounts;
      }),
      catchError(error => {
        console.warn('[DevDataService] Backend unavailable, using fallback accounts:', error.message);
        return of(this.fallbackAccounts);
      })
    );
  }

  /**
   * Fetches users for a specific account from the backend.
   * Falls back to hardcoded data if backend is unavailable.
   */
  loadUsersByAccount(accountId: string): Observable<DevUser[]> {
    const query = `
      query DevUsersByAccount($accountId: ID!) {
        devUsersByAccount(accountId: $accountId) {
          id
          email
          nome
          roles
        }
      }
    `;

    return this.http.post<GraphQLResponse<{ devUsersByAccount: DevUser[] }>>(
      this.graphqlUrl,
      { query, variables: { accountId } }
    ).pipe(
      map(response => {
        if (response.errors?.length) {
          console.warn('[DevDataService] GraphQL errors:', response.errors);
          return this.fallbackUsers;
        }
        return response.data.devUsersByAccount;
      }),
      catchError(error => {
        console.warn('[DevDataService] Backend unavailable, using fallback users:', error.message);
        return of(this.fallbackUsers);
      })
    );
  }
}
