import { Component, inject, signal, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService, Toast, ToastType, ToastPosition } from '../../services/toast.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast.component.html',
  styleUrl: './toast.component.scss'
})
export class ToastComponent implements OnDestroy {
  private readonly toastService = inject(ToastService);
  private subscription: Subscription;

  readonly toasts = signal<Toast[]>([]);
  readonly position = signal<ToastPosition>('top-right');

  constructor() {
    this.subscription = this.toastService.toasts$.subscribe(toasts => {
      this.toasts.set(toasts);
    });

    this.subscription.add(
      this.toastService.position$.subscribe(position => {
        this.position.set(position);
      })
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  remove(id: string): void {
    this.toastService.remove(id);
  }

  pause(id: string): void {
    this.toastService.pause(id);
  }

  resume(id: string): void {
    this.toastService.resume(id);
  }
}
