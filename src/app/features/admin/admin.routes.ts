import { Routes } from '@angular/router';
import { PlaceholderPageComponent } from '../../shared/components/placeholder-page/placeholder-page.component';
import { adminGuard } from '../../core/guards/permission.guard';

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    redirectTo: 'aprovacoes',
    pathMatch: 'full'
  },
  // Aprovacoes
  {
    path: 'aprovacoes',
    canActivate: [adminGuard],
    loadComponent: () => import('./components/approval-list/approval-list.component')
      .then(m => m.ApprovalListComponent),
    data: { title: 'Aprovacoes', subtitle: 'Gerenciar solicitacoes pendentes' }
  },
  // Usuarios
  {
    path: 'usuarios',
    canActivate: [adminGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Usuarios', subtitle: 'Gerenciar usuarios do sistema' }
  },
  {
    path: 'usuarios/criar',
    canActivate: [adminGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Criar Usuario', subtitle: 'Cadastrar novo usuario no sistema' }
  },
  {
    path: 'usuarios/editar',
    canActivate: [adminGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Editar Usuario', subtitle: 'Modificar dados de usuario existente' }
  },
  {
    path: 'usuarios/remover',
    canActivate: [adminGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Remover/Inativar Usuario', subtitle: 'Excluir ou desativar usuario' }
  },
  {
    path: 'usuarios/:id',
    canActivate: [adminGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Detalhes do Usuario', subtitle: 'Visualizar usuario' }
  },
  {
    path: 'usuarios/:id/editar',
    canActivate: [adminGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Editar Usuario', subtitle: 'Modificar dados de usuario existente' }
  },
  // Permissoes
  {
    path: 'permissoes',
    canActivate: [adminGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Permissoes', subtitle: 'Gerenciar permissoes do sistema' }
  },
  {
    path: 'permissoes/criar',
    canActivate: [adminGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Criar Permissao', subtitle: 'Cadastrar nova permissao' }
  },
  {
    path: 'permissoes/editar',
    canActivate: [adminGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Editar Permissao', subtitle: 'Modificar permissao existente' }
  },
  {
    path: 'permissoes/remover',
    canActivate: [adminGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Remover Permissao', subtitle: 'Excluir permissao' }
  },
  {
    path: 'permissoes/:id',
    canActivate: [adminGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Detalhes da Permissao', subtitle: 'Visualizar permissao' }
  },
  {
    path: 'permissoes/:id/editar',
    canActivate: [adminGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Editar Permissao', subtitle: 'Modificar permissao existente' }
  }
];
