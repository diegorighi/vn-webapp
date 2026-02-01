import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-access-denied',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="access-denied">
      <div class="access-denied__content">
        <div class="access-denied__icon">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
          </svg>
        </div>
        <h1 class="access-denied__title">Acesso Negado</h1>
        <p class="access-denied__message">
          Voce nao tem permissao para acessar esta pagina.
        </p>
        <a routerLink="/dashboard" class="access-denied__button">
          Voltar ao Dashboard
        </a>
      </div>
    </div>
  `,
  styles: [`
    .access-denied {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
      padding: 2rem;
    }

    .access-denied__content {
      text-align: center;
      max-width: 40rem;
    }

    .access-denied__icon {
      width: 8rem;
      height: 8rem;
      margin: 0 auto 2rem;
      background: #fee2e2;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;

      svg {
        width: 4rem;
        height: 4rem;
        color: #dc2626;
      }
    }

    .access-denied__title {
      font-size: 2.4rem;
      font-weight: 600;
      color: #1f2937;
      margin: 0 0 1rem;
    }

    .access-denied__message {
      font-size: 1.6rem;
      color: #6b7280;
      margin: 0 0 2rem;
    }

    .access-denied__button {
      display: inline-flex;
      align-items: center;
      padding: 1.2rem 2.4rem;
      background: linear-gradient(135deg, #d4a574 0%, #b8956e 100%);
      color: #1a1a2e;
      font-size: 1.4rem;
      font-weight: 600;
      text-decoration: none;
      border-radius: 0.8rem;
      transition: transform 0.2s, box-shadow 0.2s;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(212, 165, 116, 0.4);
      }
    }
  `]
})
export class AccessDeniedComponent {}
