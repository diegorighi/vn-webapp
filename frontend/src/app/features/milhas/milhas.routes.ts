import { Routes } from '@angular/router';
import { PlaceholderPageComponent } from '../../shared/components/placeholder-page/placeholder-page.component';
import { viewGuard, createGuard, editGuard, deleteGuard } from '../../core/guards/permission.guard';

export const MILHAS_ROUTES: Routes = [
  {
    path: '',
    canActivate: [viewGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Milhas', subtitle: 'Gerenciar transacoes de milhas' }
  },
  {
    path: 'registrar',
    canActivate: [createGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Registrar Transacao', subtitle: 'Registrar compra, venda ou bonus de milhas' }
  },
  {
    path: 'editar',
    canActivate: [editGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Editar Milhas', subtitle: 'Modificar registros de milhas existentes' }
  },
  {
    path: 'remover',
    canActivate: [deleteGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Remover Milhas', subtitle: 'Excluir registros de milhas' }
  },
  {
    path: ':id',
    canActivate: [viewGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Detalhes da Transacao', subtitle: 'Visualizar transacao de milhas' }
  }
];
