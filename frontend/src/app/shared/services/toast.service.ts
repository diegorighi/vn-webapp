import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export type ToastType = 'success' | 'error' | 'warning' | 'info';
export type ToastPosition = 'top-right' | 'top-left' | 'bottom-right' | 'bottom-left' | 'top-center' | 'bottom-center';

export interface Toast {
  id: string;
  type: ToastType;
  title?: string;
  message: string;
  duration: number;
  state: 'entering' | 'visible' | 'exiting';
  paused: boolean;
}

interface ToastOptions {
  title?: string;
  duration?: number;
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private readonly toastsSubject = new BehaviorSubject<Toast[]>([]);
  private readonly positionSubject = new BehaviorSubject<ToastPosition>('top-right');
  private readonly defaultDuration = 5000;

  readonly toasts$: Observable<Toast[]> = this.toastsSubject.asObservable();
  readonly position$: Observable<ToastPosition> = this.positionSubject.asObservable();

  private toasts: Toast[] = [];

  setPosition(position: ToastPosition): void {
    this.positionSubject.next(position);
  }

  success(message: string, options?: ToastOptions): string {
    return this.add('success', message, options);
  }

  error(message: string, options?: ToastOptions): string {
    return this.add('error', message, { duration: 0, ...options }); // Error toasts don't auto-dismiss by default
  }

  warning(message: string, options?: ToastOptions): string {
    return this.add('warning', message, options);
  }

  info(message: string, options?: ToastOptions): string {
    return this.add('info', message, options);
  }

  private add(type: ToastType, message: string, options?: ToastOptions): string {
    const id = this.generateId();
    const toast: Toast = {
      id,
      type,
      title: options?.title,
      message,
      duration: options?.duration ?? this.defaultDuration,
      state: 'entering',
      paused: false
    };

    // Add new toast to beginning of array
    this.toasts = [toast, ...this.toasts];
    this.toastsSubject.next([...this.toasts]);

    // Animate in
    setTimeout(() => {
      const index = this.toasts.findIndex(t => t.id === id);
      if (index !== -1) {
        this.toasts[index] = { ...this.toasts[index], state: 'visible' };
        this.toastsSubject.next([...this.toasts]);
      }
    }, 50);

    // Auto dismiss
    if (toast.duration > 0) {
      setTimeout(() => {
        this.remove(id);
      }, toast.duration + 300); // Add animation time
    }

    return id;
  }

  remove(id: string): void {
    const index = this.toasts.findIndex(t => t.id === id);
    if (index === -1) return;

    // Animate out
    this.toasts[index] = { ...this.toasts[index], state: 'exiting' };
    this.toastsSubject.next([...this.toasts]);

    // Remove from array after animation
    setTimeout(() => {
      this.toasts = this.toasts.filter(t => t.id !== id);
      this.toastsSubject.next([...this.toasts]);
    }, 300);
  }

  removeAll(): void {
    // Animate all out
    this.toasts = this.toasts.map(t => ({ ...t, state: 'exiting' }));
    this.toastsSubject.next([...this.toasts]);

    // Clear after animation
    setTimeout(() => {
      this.toasts = [];
      this.toastsSubject.next([]);
    }, 300);
  }

  pause(id: string): void {
    const index = this.toasts.findIndex(t => t.id === id);
    if (index !== -1) {
      this.toasts[index] = { ...this.toasts[index], paused: true };
      this.toastsSubject.next([...this.toasts]);
    }
  }

  resume(id: string): void {
    const index = this.toasts.findIndex(t => t.id === id);
    if (index !== -1) {
      this.toasts[index] = { ...this.toasts[index], paused: false };
      this.toastsSubject.next([...this.toasts]);
    }
  }

  private generateId(): string {
    return `toast-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
  }
}
