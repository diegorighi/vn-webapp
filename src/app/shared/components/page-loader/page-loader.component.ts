import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-page-loader',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-loader" [class.page-loader--fullscreen]="fullscreen()">
      <div class="page-loader__content">
        <div class="page-loader__spinner">
          <div class="page-loader__spinner-ring"></div>
          <div class="page-loader__spinner-ring"></div>
          <div class="page-loader__spinner-ring"></div>
        </div>
        @if (message()) {
          <p class="page-loader__message">{{ message() }}</p>
        }
        @if (showProgress()) {
          <div class="page-loader__progress">
            <div class="page-loader__progress-bar" [style.width.%]="progress()"></div>
          </div>
          <span class="page-loader__progress-text">{{ progress() }}%</span>
        }
      </div>
    </div>
  `,
  styleUrl: './page-loader.component.scss'
})
export class PageLoaderComponent {
  readonly fullscreen = input(true);
  readonly message = input<string>('Carregando...');
  readonly showProgress = input(false);
  readonly progress = input(0);
}
