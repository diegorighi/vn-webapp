import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthService } from './auth.service';

export interface GraphQLResponse<T> {
  data: T;
  errors?: GraphQLError[];
}

export interface GraphQLError {
  message: string;
  locations?: { line: number; column: number }[];
  path?: string[];
  extensions?: Record<string, unknown>;
}

@Injectable({
  providedIn: 'root'
})
export class GraphQLService {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);
  private readonly endpoint = environment.graphqlEndpoint || `${environment.apiUrl}/graphql`;

  query<T>(query: string, variables?: Record<string, unknown>): Observable<T> {
    return this.http.post<GraphQLResponse<T>>(
      this.endpoint,
      { query, variables },
      { headers: this.getHeaders() }
    ).pipe(
      map(response => {
        if (response.errors && response.errors.length > 0) {
          throw new Error(response.errors[0].message);
        }
        return response.data;
      })
    );
  }

  mutate<T>(mutation: string, variables?: Record<string, unknown>): Observable<T> {
    return this.query<T>(mutation, variables);
  }

  private getHeaders(): HttpHeaders {
    const user = this.authService.user();
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    // Add tenant/user headers for backend context (DEV mode)
    if (user) {
      headers = headers
        .set('X-Tenant-Id', user.tenantId)
        .set('X-User-Email', user.email)
        .set('X-User-Roles', user.roles.join(','));
    }

    return headers;
  }
}
