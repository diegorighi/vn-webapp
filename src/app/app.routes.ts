import { Routes } from '@angular/router';
import { MainLayoutComponent } from './core/layouts/main-layout/main-layout.component';
import { authGuard, guestGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  // Landing Page (pública)
  {
    path: 'landing',
    loadComponent: () => import('./features/landing-page/landing-page.component')
      .then(m => m.LandingPageComponent)
  },
  // Auth routes (outside main layout) - apenas para usuarios nao autenticados
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () => import('./features/auth/login.component')
      .then(m => m.LoginComponent)
  },
  // Pagina de acesso negado
  {
    path: 'acesso-negado',
    loadComponent: () => import('./features/auth/access-denied.component')
      .then(m => m.AccessDeniedComponent)
  },
  // Redirecionar raiz para landing page
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'landing'
  },
  // Main layout (protegido)
  {
    path: 'app',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard.component')
          .then(m => m.DashboardComponent)
      },
      // Milhas
      {
        path: 'milhas',
        loadChildren: () => import('./features/milhas/milhas.routes')
          .then(m => m.MILHAS_ROUTES)
      },
      // Programas
      {
        path: 'programas',
        loadChildren: () => import('./features/programas/programas.routes')
          .then(m => m.PROGRAMAS_ROUTES)
      },
      // Admin
      {
        path: 'admin',
        loadChildren: () => import('./features/admin/admin.routes')
          .then(m => m.ADMIN_ROUTES)
      },
      // Clientes
      {
        path: 'clientes',
        loadChildren: () => import('./features/clientes/clientes.routes')
          .then(m => m.CLIENTES_ROUTES)
      },
      // Viagens
      {
        path: 'viagens',
        loadChildren: () => import('./features/viagens/viagens.routes')
          .then(m => m.VIAGENS_ROUTES)
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'landing'
  }
];
