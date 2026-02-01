import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

type UserRole = 'ROOT' | 'ADMIN' | 'MANAGER' | 'OPERATOR' | 'VIEWER';
type Permission = 'view' | 'create' | 'edit' | 'delete' | 'admin';

const ROLE_PERMISSIONS: Record<UserRole, Permission[]> = {
  ROOT: ['view', 'create', 'edit', 'delete', 'admin'],
  ADMIN: ['view', 'create', 'edit', 'delete', 'admin'],
  MANAGER: ['view', 'create', 'edit'],
  OPERATOR: ['view', 'create'],
  VIEWER: ['view']
};

function getUserPermissions(roles: string[]): Set<Permission> {
  const permissions = new Set<Permission>();
  roles.forEach(role => {
    const rolePerms = ROLE_PERMISSIONS[role as UserRole];
    if (rolePerms) {
      rolePerms.forEach(perm => permissions.add(perm));
    }
  });
  return permissions;
}

/**
 * Guard que verifica se o usuario tem permissao para acessar a rota.
 * Uso: canActivate: [permissionGuard(['create'])]
 */
export function permissionGuard(requiredPermissions: Permission[]): CanActivateFn {
  return (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    const user = authService.user();
    const userRoles = user?.roles ?? ['VIEWER'];
    const userPermissions = getUserPermissions(userRoles);

    const hasPermission = requiredPermissions.some(perm => userPermissions.has(perm));

    if (hasPermission) {
      return true;
    }

    // Redireciona para pagina de acesso negado
    router.navigate(['/acesso-negado']);
    return false;
  };
}

// Guards pre-configurados para facilitar uso
export const viewGuard: CanActivateFn = permissionGuard(['view']);
export const createGuard: CanActivateFn = permissionGuard(['create']);
export const editGuard: CanActivateFn = permissionGuard(['edit']);
export const deleteGuard: CanActivateFn = permissionGuard(['delete']);
export const adminGuard: CanActivateFn = permissionGuard(['admin']);
