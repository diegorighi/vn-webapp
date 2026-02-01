import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';
import { environment } from './environments/environment';

bootstrapApplication(App, appConfig)
  .catch((err) => {
    if (!environment.production) {
      console.error('Bootstrap error:', err);
    }
    // Em producao, mostrar pagina de erro generica
    const errorDiv = document.createElement('div');
    errorDiv.innerHTML = `
      <div style="display:flex;align-items:center;justify-content:center;height:100vh;font-family:system-ui;">
        <div style="text-align:center;">
          <h1 style="color:#dc2626;">Erro ao carregar aplicacao</h1>
          <p>Por favor, recarregue a pagina ou tente novamente mais tarde.</p>
        </div>
      </div>
    `;
    document.body.appendChild(errorDiv);
  });
