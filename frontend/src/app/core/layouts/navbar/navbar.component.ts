import { Component, inject, signal, HostListener, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { SidebarService } from '../../services/sidebar.service';
import { ThemeService } from '../../services/theme.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {
  private readonly sidebarService = inject(SidebarService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly elementRef = inject(ElementRef);
  readonly themeService = inject(ThemeService);

  readonly isSidebarOpen = this.sidebarService.isOpen;
  readonly user = this.authService.user;
  readonly isProfileMenuOpen = signal(false);

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const clickedInside = this.elementRef.nativeElement.contains(event.target);
    if (!clickedInside && this.isProfileMenuOpen()) {
      this.closeProfileMenu();
    }
  }

  get showUserManagement(): boolean {
    return this.authService.hasAnyRole(['ROOT', 'ADMIN']);
  }

  toggleSidebar(): void {
    this.sidebarService.toggle();
  }

  toggleTheme(): void {
    this.themeService.toggleTheme();
  }

  toggleProfileMenu(): void {
    this.isProfileMenuOpen.update(v => !v);
  }

  closeProfileMenu(): void {
    this.isProfileMenuOpen.set(false);
  }

  logout(): void {
    this.authService.logout();
    this.closeProfileMenu();
  }

  async resetPassword(): Promise<void> {
    // TODO: Implementar fluxo de reset de senha
    alert('Funcionalidade de reset de senha em desenvolvimento');
    this.closeProfileMenu();
  }

  navigateToUserManagement(): void {
    this.router.navigate(['/admin/usuarios']);
    this.closeProfileMenu();
  }
}
