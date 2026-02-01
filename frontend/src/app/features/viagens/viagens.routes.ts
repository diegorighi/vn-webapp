import { Routes } from '@angular/router';
import { PlaceholderPageComponent } from '../../shared/components/placeholder-page/placeholder-page.component';
import { viewGuard, createGuard, editGuard, deleteGuard } from '../../core/guards/permission.guard';

export const VIAGENS_ROUTES: Routes = [
  {
    path: '',
    canActivate: [viewGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Viagens', subtitle: 'Gerenciar viagens' }
  },
  {
    path: 'nova',
    canActivate: [createGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Nova Viagem', subtitle: 'Registrar nova viagem' }
  },
  {
    path: 'editar',
    canActivate: [editGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Editar Viagem', subtitle: 'Modificar viagem existente' }
  },
  {
    path: 'remover',
    canActivate: [deleteGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Remover Viagem', subtitle: 'Excluir viagem' }
  },
  {
    path: ':id',
    canActivate: [viewGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Detalhes da Viagem', subtitle: 'Visualizar viagem' }
  },
  {
    path: ':id/editar',
    canActivate: [editGuard],
    component: PlaceholderPageComponent,
    data: { title: 'Editar Viagem', subtitle: 'Modificar viagem existente' }
  }
];
