import { Injectable, signal, computed, inject } from '@angular/core';
import { AuthService } from './auth.service';

type UserRole = 'ROOT' | 'ADMIN' | 'MANAGER' | 'OPERATOR' | 'VIEWER';
type Permission = 'view' | 'create' | 'edit' | 'delete' | 'admin';

export interface MenuItem {
  label: string;
  icon: string;
  route?: string;
  children?: MenuItem[];
  expanded?: boolean;
  permissions?: Permission[]; // Permissoes necessarias para ver o item
}

// Mapeamento de roles para permissoes
const ROLE_PERMISSIONS: Record<UserRole, Permission[]> = {
  ROOT: ['view', 'create', 'edit', 'delete', 'admin'],
  ADMIN: ['view', 'create', 'edit', 'delete', 'admin'],
  MANAGER: ['view', 'create', 'edit'],
  OPERATOR: ['view', 'create'],
  VIEWER: ['view']
};

@Injectable({
  providedIn: 'root'
})
export class SidebarService {
  private readonly authService = inject(AuthService);
  private readonly _isOpen = signal<boolean>(true);
  private readonly _isMobile = signal<boolean>(false);

  readonly isOpen = this._isOpen.asReadonly();
  readonly isMobile = this._isMobile.asReadonly();
  readonly isVisible = computed(() => this._isOpen());

  // Menu completo com permissoes
  private readonly _allMenuItems = signal<MenuItem[]>([
    {
      label: 'Dashboard',
      icon: 'dashboard',
      route: '/app/dashboard',
      permissions: ['view']
    },
    {
      label: 'Milhas',
      icon: 'flight',
      permissions: ['view'],
      children: [
        { label: 'Listar', icon: 'list', route: '/app/milhas', permissions: ['view'] },
        { label: 'Registrar', icon: 'add', route: '/app/milhas/registrar', permissions: ['create'] },
        { label: 'Editar', icon: 'edit', route: '/app/milhas/editar', permissions: ['edit'] },
        { label: 'Remover', icon: 'delete', route: '/app/milhas/remover', permissions: ['delete'] }
      ]
    },
    {
      label: 'Programas',
      icon: 'loyalty',
      permissions: ['view'],
      children: [
        { label: 'Listar', icon: 'list', route: '/app/programas', permissions: ['view'] },
        { label: 'Incluir', icon: 'add', route: '/app/programas/incluir', permissions: ['create'] },
        { label: 'Editar', icon: 'edit', route: '/app/programas/editar', permissions: ['edit'] },
        { label: 'Remover', icon: 'delete', route: '/app/programas/remover', permissions: ['delete'] }
      ]
    },
    {
      label: 'Clientes',
      icon: 'groups',
      permissions: ['view'],
      children: [
        { label: 'Listar', icon: 'list', route: '/app/clientes', permissions: ['view'] },
        { label: 'Cadastrar', icon: 'person_add', route: '/app/clientes/novo', permissions: ['create'] },
        { label: 'Editar', icon: 'edit', route: '/app/clientes/editar', permissions: ['edit'] },
        { label: 'Remover/Inativar', icon: 'person_off', route: '/app/clientes/remover', permissions: ['delete'] }
      ]
    },
    {
      label: 'Viagens',
      icon: 'luggage',
      permissions: ['view'],
      children: [
        { label: 'Listar', icon: 'list', route: '/app/viagens', permissions: ['view'] },
        { label: 'Nova Viagem', icon: 'add', route: '/app/viagens/nova', permissions: ['create'] },
        { label: 'Editar', icon: 'edit', route: '/app/viagens/editar', permissions: ['edit'] },
        { label: 'Remover', icon: 'delete', route: '/app/viagens/remover', permissions: ['delete'] }
      ]
    },
    {
      label: 'Administração',
      icon: 'admin_panel_settings',
      permissions: ['admin'],
      children: [
        {
          label: 'Aprovações',
          icon: 'fact_check',
          route: '/app/admin/aprovacoes',
          permissions: ['admin']
        },
        {
          label: 'Usuários',
          icon: 'people',
          permissions: ['admin'],
          children: [
            { label: 'Listar', icon: 'list', route: '/app/admin/usuarios', permissions: ['admin'] },
            { label: 'Criar', icon: 'person_add', route: '/app/admin/usuarios/criar', permissions: ['admin'] },
            { label: 'Editar', icon: 'edit', route: '/app/admin/usuarios/editar', permissions: ['admin'] },
            { label: 'Remover/Inativar', icon: 'person_off', route: '/app/admin/usuarios/remover', permissions: ['admin'] }
          ]
        },
        {
          label: 'Permissões',
          icon: 'security',
          permissions: ['admin'],
          children: [
            { label: 'Listar', icon: 'list', route: '/app/admin/permissoes', permissions: ['admin'] },
            { label: 'Criar', icon: 'add', route: '/app/admin/permissoes/criar', permissions: ['admin'] },
            { label: 'Editar', icon: 'edit', route: '/app/admin/permissoes/editar', permissions: ['admin'] },
            { label: 'Remover', icon: 'delete', route: '/app/admin/permissoes/remover', permissions: ['admin'] }
          ]
        }
      ]
    }
  ]);

  // Menu filtrado baseado nas permissoes do usuario
  readonly menuItems = computed(() => {
    const user = this.authService.user();
    const userRoles = (user?.roles ?? ['VIEWER']) as UserRole[];

    // Combina permissoes de todas as roles do usuario
    const userPermissions = new Set<Permission>();
    userRoles.forEach(role => {
      ROLE_PERMISSIONS[role]?.forEach(perm => userPermissions.add(perm));
    });

    return this.filterMenuByPermissions(this._allMenuItems(), userPermissions);
  });

  private filterMenuByPermissions(items: MenuItem[], userPermissions: Set<Permission>): MenuItem[] {
    return items
      .filter(item => this.hasPermission(item, userPermissions))
      .map(item => {
        if (item.children) {
          const filteredChildren = this.filterMenuByPermissions(item.children, userPermissions);
          // So mostra o item pai se tiver filhos visiveis
          if (filteredChildren.length === 0) {
            return null;
          }
          return { ...item, children: filteredChildren };
        }
        return item;
      })
      .filter((item): item is MenuItem => item !== null);
  }

  private hasPermission(item: MenuItem, userPermissions: Set<Permission>): boolean {
    if (!item.permissions || item.permissions.length === 0) {
      return true; // Sem restricao
    }
    return item.permissions.some(perm => userPermissions.has(perm));
  }

  toggle(): void {
    this._isOpen.update(value => !value);
  }

  open(): void {
    this._isOpen.set(true);
  }

  close(): void {
    this._isOpen.set(false);
  }

  setMobile(isMobile: boolean): void {
    const wasMobile = this._isMobile();
    this._isMobile.set(isMobile);

    if (!wasMobile && isMobile) {
      this._isOpen.set(false);
    }
    if (wasMobile && !isMobile) {
      this._isOpen.set(true);
    }
  }

  toggleMenuItem(item: MenuItem): void {
    this._allMenuItems.update(items => this.updateExpanded(items, item));
  }

  private updateExpanded(items: MenuItem[], targetItem: MenuItem): MenuItem[] {
    const isTopLevel = items.some(i => i.label === targetItem.label);

    return items.map(menuItem => {
      if (isTopLevel) {
        if (menuItem.label === targetItem.label) {
          return { ...menuItem, expanded: !menuItem.expanded };
        }
        return { ...menuItem, expanded: false };
      }

      if (menuItem.children) {
        return {
          ...menuItem,
          children: this.updateExpanded(menuItem.children, targetItem)
        };
      }
      return menuItem;
    });
  }
}
