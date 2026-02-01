import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, NavigationEnd, RouterLink } from '@angular/router';
import { filter, map } from 'rxjs/operators';
import { toSignal } from '@angular/core/rxjs-interop';

interface Breadcrumb {
  label: string;
  url: string;
  isLast: boolean;
}

@Component({
  selector: 'app-breadcrumbs',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './breadcrumbs.html',
  styleUrl: './breadcrumbs.scss'
})
export class BreadcrumbsComponent {
  private router = inject(Router);

  private readonly labelMap: Record<string, string> = {
    'dashboard': 'Dashboard',
    'milhas': 'Milhas',
    'programas': 'Programas',
    'admin': 'Administracao',
    'clientes': 'Clientes',
    'viagens': 'Viagens',
    'lista': 'Lista',
    'listar': 'Listar',
    'novo': 'Novo',
    'incluir': 'Incluir',
    'registrar': 'Registrar',
    'cadastrar': 'Cadastrar',
    'editar': 'Editar',
    'remover': 'Remover',
    'detalhes': 'Detalhes',
    'configuracoes': 'Configuracoes',
    'usuarios': 'Usuarios',
    'permissoes': 'Permissoes',
    'relatorios': 'Relatorios',
    'transacoes': 'Transacoes',
    'saldo': 'Saldo',
    'historico': 'Historico',
    'criar': 'Criar',
    'nova': 'Nova',
  };

  breadcrumbs = toSignal(
    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd),
      map(event => this.buildBreadcrumbs(event.urlAfterRedirects))
    ),
    { initialValue: this.buildBreadcrumbs(this.router.url) }
  );

  private buildBreadcrumbs(url: string): Breadcrumb[] {
    const segments = url.split('/').filter(segment => segment && !segment.startsWith('?'));

    if (segments.length === 0 || (segments.length === 1 && segments[0] === 'dashboard')) {
      return [];
    }

    const breadcrumbs: Breadcrumb[] = [];
    let currentUrl = '';

    segments.forEach((segment, index) => {
      currentUrl += `/${segment}`;
      const isLast = index === segments.length - 1;

      // Ignora IDs numericos ou UUIDs no breadcrumb label
      const isId = /^[0-9a-f-]{8,}$/i.test(segment) || /^\d+$/.test(segment);
      const label = isId ? 'Detalhes' : this.getLabel(segment);

      breadcrumbs.push({
        label,
        url: currentUrl,
        isLast
      });
    });

    return breadcrumbs;
  }

  private getLabel(segment: string): string {
    return this.labelMap[segment.toLowerCase()] || this.capitalize(segment);
  }

  private capitalize(str: string): string {
    return str.charAt(0).toUpperCase() + str.slice(1).replace(/-/g, ' ');
  }
}
