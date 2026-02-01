import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-placeholder-page',
  standalone: true,
  imports: [CommonModule],
  template: `
    <article class="placeholder-page">
      <header class="placeholder-page__header">
        <h1 class="placeholder-page__title">{{ title }}</h1>
        <p class="placeholder-page__subtitle">{{ subtitle }}</p>
      </header>
      <section class="placeholder-page__content">
        <div class="placeholder-page__icon">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="64" height="64">
            <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
          </svg>
        </div>
        <p class="placeholder-page__message">Esta página está em desenvolvimento.</p>
      </section>
    </article>
  `,
  styles: [`
    @use '../../../../styles/abstracts' as *;

    .placeholder-page {
      max-width: $container-max-width;
      margin: 0 auto;

      &__header {
        margin-bottom: $spacing-xl;
      }

      &__title {
        font-size: $font-size-2xl;
        font-weight: $font-weight-bold;
        color: $color-text-primary;
        margin-bottom: $spacing-xs;

        @include tablet {
          font-size: $font-size-3xl;
        }
      }

      &__subtitle {
        font-size: $font-size-sm;
        color: $color-text-secondary;
        margin: 0;
      }

      &__content {
        @include card;
        @include flex-column-center;
        min-height: 30rem;
        gap: $spacing-md;
      }

      &__icon {
        color: $color-text-muted;
        opacity: 0.5;
      }

      &__message {
        font-size: $font-size-base;
        color: $color-text-muted;
        margin: 0;
      }
    }
  `]
})
export class PlaceholderPageComponent {
  @Input() title = 'Página';
  @Input() subtitle = 'Em construção';
}
