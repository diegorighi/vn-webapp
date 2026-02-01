import { Injectable, signal, effect } from '@angular/core';

export type Theme = 'light' | 'dark' | 'system';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly STORAGE_KEY = 'vn-theme';
  
  // Signal para o tema atual
  readonly currentTheme = signal<Theme>(this.getStoredTheme());
  
  // Signal computado para o tema efetivo (light ou dark)
  readonly effectiveTheme = signal<'light' | 'dark'>('light');
  
  private mediaQuery: MediaQueryList;

  constructor() {
    // Media query para detectar preferência do sistema
    this.mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
    
    // Listener para mudanças na preferência do sistema
    this.mediaQuery.addEventListener('change', (e) => {
      if (this.currentTheme() === 'system') {
        this.applyTheme(e.matches ? 'dark' : 'light');
      }
    });

    // Efeito para aplicar tema quando mudar
    effect(() => {
      const theme = this.currentTheme();
      this.storeTheme(theme);
      
      if (theme === 'system') {
        this.applyTheme(this.mediaQuery.matches ? 'dark' : 'light');
      } else {
        this.applyTheme(theme);
      }
    });

    // Aplicar tema inicial
    this.initTheme();
  }

  private initTheme(): void {
    const theme = this.currentTheme();
    if (theme === 'system') {
      this.applyTheme(this.mediaQuery.matches ? 'dark' : 'light');
    } else {
      this.applyTheme(theme);
    }
  }

  private getStoredTheme(): Theme {
    if (typeof localStorage === 'undefined') return 'system';
    const stored = localStorage.getItem(this.STORAGE_KEY) as Theme;
    return stored || 'system';
  }

  private storeTheme(theme: Theme): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem(this.STORAGE_KEY, theme);
    }
  }

  private applyTheme(theme: 'light' | 'dark'): void {
    this.effectiveTheme.set(theme);
    
    const html = document.documentElement;
    
    if (theme === 'dark') {
      html.classList.add('dark');
      html.classList.remove('light');
    } else {
      html.classList.add('light');
      html.classList.remove('dark');
    }

    // Atualizar meta tag theme-color para mobile
    this.updateMetaThemeColor(theme);
  }

  private updateMetaThemeColor(theme: 'light' | 'dark'): void {
    const metaThemeColor = document.querySelector('meta[name="theme-color"]');
    if (metaThemeColor) {
      const color = theme === 'dark' ? '#0f172a' : '#ffffff';
      metaThemeColor.setAttribute('content', color);
    }
  }

  setTheme(theme: Theme): void {
    this.currentTheme.set(theme);
  }

  toggleTheme(): void {
    const current = this.effectiveTheme();
    const newTheme = current === 'light' ? 'dark' : 'light';
    this.currentTheme.set(newTheme);
  }

  isDark(): boolean {
    return this.effectiveTheme() === 'dark';
  }
}
