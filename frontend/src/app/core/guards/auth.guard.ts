import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { environment } from '../../../environments/environment';

export const authGuard: CanActivateFn = (route, state) => {
  // DEV: auth desabilitado, sempre permite
  if (!environment.authEnabled) {
    return true;
  }

  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true;
  }

  // Salvar URL de retorno para redirecionar apos login
  router.navigate(['/login'], {
    queryParams: { returnUrl: state.url }
  });

  return false;
};

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    router.navigate(['/login'], {
      queryParams: { returnUrl: state.url }
    });
    return false;
  }

  const requiredRoles = route.data['roles'] as string[] | undefined;

  if (!requiredRoles || requiredRoles.length === 0) {
    return true;
  }

  if (authService.hasAnyRole(requiredRoles)) {
    return true;
  }

  // Usuario nao tem permissao
  router.navigate(['/acesso-negado']);
  return false;
};

export const guestGuard: CanActivateFn = () => {
  const router = inject(Router);

  // DEV: auth desabilitado, redireciona direto para dashboard
  if (!environment.authEnabled) {
    router.navigate(['/app/dashboard']);
    return false;
  }

  const authService = inject(AuthService);

  if (!authService.isAuthenticated()) {
    return true;
  }

  // Usuario ja autenticado, redirecionar para dashboard
  router.navigate(['/app/dashboard']);
  return false;
};
