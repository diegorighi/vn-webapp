import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly authService = inject(AuthService);

  readonly isLoading = signal(false);
  readonly showPassword = signal(false);
  readonly rememberMe = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly currentYear = new Date().getFullYear();

  loginForm: FormGroup;
  private returnUrl: string = '/app/dashboard';

  constructor() {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });

    // Capturar URL de retorno se existir
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/app/dashboard';

    // Verificar se sessao expirou
    if (this.route.snapshot.queryParams['sessionExpired']) {
      this.errorMessage.set('Sua sessao expirou. Faca login novamente.');
    }
  }

  togglePassword(): void {
    this.showPassword.update(value => !value);
  }

  toggleRememberMe(): void {
    this.rememberMe.update(value => !value);
  }

  async onSubmit(): Promise<void> {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    try {
      const { email, password } = this.loginForm.value;
      const success = await this.authService.login(email, password);

      if (success) {
        this.router.navigateByUrl(this.returnUrl);
      } else {
        this.errorMessage.set('Email ou senha invalidos');
      }
    } catch {
      this.errorMessage.set('Erro ao conectar com o servidor. Tente novamente.');
    } finally {
      this.isLoading.set(false);
    }
  }

  get email() {
    return this.loginForm.get('email');
  }

  get password() {
    return this.loginForm.get('password');
  }
}
