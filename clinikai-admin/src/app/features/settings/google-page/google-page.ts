import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GoogleIntegrationService, GoogleStatusDto } from '../../../core/api/google-integration.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  standalone: true,
  imports: [CommonModule],
  templateUrl: './google-page.html',
  styleUrl: './google-page.css',
})
export class GoogleSettingsPage {
  private google = inject(GoogleIntegrationService);

  loading = signal(false);
  error = signal<string | null>(null);

  status = signal<GoogleStatusDto | null>(null);

  private route = inject(ActivatedRoute);
  toast = signal<string | null>(null);

  ngOnInit() {
    // feedback do redirect do callback
    const connected = this.route.snapshot.queryParamMap.get('connected');
    if (connected === '1') this.toast.set('Conta Google conectada com sucesso ✅');
    if (connected === '0') this.toast.set('Não foi possível conectar a conta Google.');

    this.loadStatus();
  }

  loadStatus() {
    this.loading.set(true);
    this.error.set(null);

    this.google.status().subscribe({
      next: (res) => {
        this.status.set(res);
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(err?.error?.message ?? 'Não foi possível carregar o status do Google.');
      },
    });
  }

  connect() {
    this.loading.set(true);
    this.error.set(null);

    this.google.getAuthorizeUrl().subscribe({
      next: (res) => {
        this.loading.set(false);
        // redireciona para o Google OAuth
        console.log('Redirecting to Google OAuth URL:', res.url);
        window.location.href = res.url;
      },
      error: (err) => {
        console.error('Failed to get Google OAuth URL:', err);
        this.loading.set(false);
        this.error.set(err?.error?.message ?? 'Não foi possível iniciar a conexão com o Google.');
      },
    });
  }

  disconnect() {
  this.loading.set(true);
  this.error.set(null);

    this.google.disconnect().subscribe({
      next: () => {
        this.loading.set(false);
        this.toast.set('Conta Google desconectada.');
        this.loadStatus();
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(err?.error?.message ?? 'Não foi possível desconectar.');
      },
    });
  }

  clearToast() {
    this.toast.set(null);
  }
}
