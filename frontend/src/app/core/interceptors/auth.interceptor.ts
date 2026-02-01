import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // URLs que nao precisam de autenticacao
  const publicUrls = [
    '/api/auth/login',
    '/api/auth/refresh',
    'viacep.com.br'
  ];

  const isPublicUrl = publicUrls.some(url => req.url.includes(url));

  let authReq = req;

  if (!isPublicUrl) {
    const token = authService.getAccessToken();
    const tenantId = authService.tenantId();

    if (token) {
      authReq = req.clone({
        setHeaders: {
          'Authorization': `Bearer ${token}`,
          ...(tenantId && { 'X-Tenant-ID': tenantId })
        }
      });
    }
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        // Token expirado ou invalido
        authService.logout();
        router.navigate(['/login'], {
          queryParams: { sessionExpired: true }
        });
      } else if (error.status === 403) {
        // Sem permissao
        router.navigate(['/acesso-negado']);
      }

      return throwError(() => error);
    })
  );
};
