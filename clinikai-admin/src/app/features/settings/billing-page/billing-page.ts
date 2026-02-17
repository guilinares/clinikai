import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BillingDto, BillingService, PixQrDto } from '../../../core/api/billing.service';
import { waitForAsync } from '@angular/core/testing';

@Component({
  standalone: true,
  imports: [CommonModule],
  templateUrl: './billing-page.html',
  styleUrl: './billing-page.css',
})
export class BillingPage {
  private billing = inject(BillingService);

  loading = signal(false);
  error = signal<string | null>(null);

  data = signal<BillingDto | null>(null);
  pix = signal<PixQrDto | null>(null);

  status = computed(() => this.data()?.status);

  ngOnInit() {
    this.refresh();
  }

  refresh() {
    this.loading.set(true);
    this.error.set(null);

    this.billing.getStatus().subscribe({
      next: (res) => {
        this.data.set(res);
        this.loading.set(false);

        // se precisa mostrar pix
        if (res.status === 'PENDING' || res.status === 'PAST_DUE') {
          this.loadPix();
        } else {
          this.pix.set(null);
        }
      },
      error: () => {
        this.error.set('Não foi possível carregar o status de pagamento.');
        this.loading.set(false);
      },
    });
  }

  subscribe() {
    this.loading.set(true);
    this.error.set(null);

    this.billing.subscribeBasic().subscribe({
      next: (res) => {
        this.data.set(res);
        this.loading.set(false);
        setTimeout(() => {
          this.loadPix();
        }, 10000);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Falha ao iniciar assinatura.');
        this.loading.set(false);
      },
    });

  }

  loadPix() {
    this.billing.getPix().subscribe({
      next: (pix) => this.pix.set(pix),
      error: () => this.error.set('Assinatura criada, mas não consegui obter o Pix agora. Tente atualizar.'),
    });
  }

  copyPix() {
    const payload = this.pix()?.payload;
    if (!payload) return;
    navigator.clipboard.writeText(payload);
  }
}
