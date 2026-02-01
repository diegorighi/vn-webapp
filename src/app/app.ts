import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ToastComponent } from './shared/components/toast/toast.component';
import { DevToolbarComponent } from './core/components/dev-toolbar/dev-toolbar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, ToastComponent, DevToolbarComponent],
  template: `
    <router-outlet />
    <app-toast />
    <app-dev-toolbar />
  `
})
export class App {}
