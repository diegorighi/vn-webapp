import { Routes } from '@angular/router';
import { PlaceholderPageComponent } from '../../shared/components/placeholder-page/placeholder-page.component';
import { viewGuard, createGuard, editGuard, deleteGuard } from '../../core/guards/permission.guard';

export const PROGRAMAS_ROUTES: Routes = [
  {
    path: '',
    canActivate: [viewGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Programas', subtitle: 'Gerenciar programas de milhas' }
  },
  {
    path: 'incluir',
    canActivate: [createGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Incluir Programa', subtitle: 'Cadastrar novo programa de milhas' }
  },
  {
    path: 'editar',
    canActivate: [editGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Editar Programa', subtitle: 'Modificar programa de milhas existente' }
  },
  {
    path: 'remover',
    canActivate: [deleteGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Remover Programa', subtitle: 'Excluir ou desativar programa de milhas' }
  },
  {
    path: ':id',
    canActivate: [viewGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Detalhes do Programa', subtitle: 'Visualizar programa de milhas' }
  },
  {
    path: ':id/editar',
    canActivate: [editGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Editar Programa', subtitle: 'Modificar programa de milhas existente' }
  }
];
