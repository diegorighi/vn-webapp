import { Routes } from '@angular/router';
import { viewGuard, createGuard, editGuard, deleteGuard } from '../../core/guards/permission.guard';

export const CLIENTES_ROUTES: Routes = [
  {
    path: '',
    canActivate: [viewGuard],
    loadComponent: () => import('./components/cliente-list/cliente-list.component')
      .then(m => m.ClienteListComponent),
    data: { title: 'Clientes', subtitle: 'Gerenciar clientes cadastrados' }
  },
  {
    path: 'novo',
    canActivate: [createGuard],
    loadComponent: () => import('./components/cliente-form/cliente-form.component')
      .then(m => m.ClienteFormComponent),
    data: { title: 'Novo Cliente', subtitle: 'Cadastrar novo cliente' }
  },
  {
    path: 'editar',
    canActivate: [editGuard],
    loadComponent: () => import('./components/cliente-search/cliente-search.component')
      .then(m => m.ClienteSearchComponent),
    data: { title: 'Editar Cliente', subtitle: 'Buscar cliente para editar', mode: 'editar' }
  },
  {
    path: 'remover',
    canActivate: [deleteGuard],
    loadComponent: () => import('./components/cliente-search/cliente-search.component')
      .then(m => m.ClienteSearchComponent),
    data: { title: 'Remover/Inativar Cliente', subtitle: 'Buscar cliente para remover', mode: 'remover' }
  },
  {
    path: ':id',
    canActivate: [viewGuard],
    loadComponent: () => import('./components/cliente-form/cliente-form.component')
      .then(m => m.ClienteFormComponent),
    data: { title: 'Detalhes do Cliente', subtitle: 'Visualizar dados do cliente' }
  },
  {
    path: ':id/editar',
    canActivate: [editGuard],
    loadComponent: () => import('./components/cliente-form/cliente-form.component')
      .then(m => m.ClienteFormComponent),
    data: { title: 'Editar Cliente', subtitle: 'Modificar dados do cliente' }
  }
];
