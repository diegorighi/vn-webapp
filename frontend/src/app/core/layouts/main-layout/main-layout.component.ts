import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';
import { AsideMenuComponent } from '../aside-menu/aside-menu.component';
import { FooterComponent } from '../footer/footer.component';
import { BreadcrumbsComponent } from '../../../shared/components/breadcrumbs/breadcrumbs';
import { ToastComponent } from '../../../shared/components/toast/toast.component';
import { SidebarService } from '../../services/sidebar.service';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    NavbarComponent,
    AsideMenuComponent,
    FooterComponent,
    BreadcrumbsComponent,
    ToastComponent
  ],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.scss'
})
export class MainLayoutComponent {
  private readonly sidebarService = inject(SidebarService);

  readonly isSidebarOpen = this.sidebarService.isOpen;
  readonly isMobile = this.sidebarService.isMobile;
  readonly isDevMode = !environment.authEnabled;
}
