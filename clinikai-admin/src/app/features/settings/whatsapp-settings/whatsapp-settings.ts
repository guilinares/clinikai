import { CommonModule } from '@angular/common';
import { Component, DestroyRef, OnInit, computed, effect, inject, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import {
  WhatsappSettingsService,
  ClinicWhatsappStatusResponse,
  ClinicWhatsappQrResponse,
  ApiErrorResponse,
} from '../../../core/api/clinic-whatsapp.service';

@Component({
  selector: 'app-whatsapp-settings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './whatsapp-settings.html',
  styleUrls: ['./whatsapp-settings.css'],
})
export class WhatsappSettingsComponent implements OnInit {
  private readonly api = inject(WhatsappSettingsService);
  private readonly destroyRef = inject(DestroyRef);

  readonly loadingStatus = signal(false);
  readonly loadingQr = signal(false);

  readonly status = signal<ClinicWhatsappStatusResponse | null>(null);
  qr = signal<{ status: string; qrBase64: string | null; expiresInSeconds?: number } | null>(null);

  readonly apiError = signal<ApiErrorResponse | null>(null);
  readonly genericError = signal<string | null>(null);

  readonly qrMode = signal(false);
  private qrTimer: any = null;

  qrImageSrc = computed(() => {
    const raw = this.qr()?.qrBase64;
    if (!raw) return null;

    // se já vier como data-uri, usa direto
    if (raw.startsWith('data:image/')) return raw;

    // se vier base64 puro
    return `data:image/png;base64,${raw}`;
  });

  readonly badgeClass = computed(() => {
    const s = this.status()?.status;
    if (!s) return 'badge';
    if (s === 'CONNECTED') return 'badge ok';
    if (s === 'AWAITING_QR' || s === 'PROVISIONING') return 'badge warn';
    if (s === 'DISCONNECTED' || s === 'SUSPENDED' || s === 'ERROR') return 'badge danger';
    return 'badge';
  });

  readonly canShowQr = computed(() => {
    const s = this.status()?.status;
    return s === 'AWAITING_QR' || s === 'DISCONNECTED' || s === 'PROVISIONING';
  });

  readonly createdAtText = computed(() => {
    const ms = this.status()?.created;
    return ms ? new Date(ms).toLocaleString('pt-BR') : null;
  });

  // ✅ effect criado no injection context (field initializer)
  private readonly _closeQrOnConnected = effect(
    () => {
      const st = this.status()?.status;
      if (st === 'CONNECTED') {
        this.stopQrTimer();
        this.qrMode.set(false);
        this.qr.set(null);
      }
    });

  ngOnInit(): void {
    // garante cleanup do timer ao destruir o componente
    this.destroyRef.onDestroy(() => this.stopQrTimer());

    this.refreshStatus();
  }

  refreshStatus(): void {
    this.loadingStatus.set(true);
    this.apiError.set(null);
    this.genericError.set(null);

    this.api.getStatus()
      .pipe(
        finalize(() => this.loadingStatus.set(false)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (s) => this.status.set(s),
        error: (err) => this.handleError(err),
      });
  }

  openQr(): void {
    if (!this.canShowQr()) return;
    this.qrMode.set(true);
    this.loadQrOnce(true);
  }

  closeQr(): void {
    this.qrMode.set(false);
    this.stopQrTimer();
    this.qr.set(null);
  }

  private loadQrOnce(startTimerIfNeeded: boolean): void {
    this.loadingQr.set(true);
    this.apiError.set(null);
    this.genericError.set(null);

    this.api.getQr()
      .pipe(
        finalize(() => this.loadingQr.set(false)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (qr) => {
          this.qr.set(qr);
          this.refreshStatus();

          if (startTimerIfNeeded) this.startQrTimer(qr.expiresInSeconds ?? 20);
        },
        error: (err) => {
          this.handleError(err);
          this.stopQrTimer();
        },
      });
  }

  private startQrTimer(expiresInSeconds: number): void {
    this.stopQrTimer();
    const refreshEveryMs = Math.max(10_000, (expiresInSeconds - 5) * 1000);

    this.qrTimer = setInterval(() => {
      if (!this.qrMode()) return this.stopQrTimer();
      if (this.loadingQr()) return;
      this.loadQrOnce(false);
    }, refreshEveryMs);
  }

  private stopQrTimer(): void {
    if (this.qrTimer) {
      clearInterval(this.qrTimer);
      this.qrTimer = null;
    }
  }

  private handleError(err: any): void {
    const apiErr = WhatsappSettingsService.parseApiError(err);
    if (apiErr) return this.apiError.set(apiErr);
    this.genericError.set(err?.message ?? 'Erro inesperado.');
  }
}
