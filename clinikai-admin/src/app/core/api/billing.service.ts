import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export type BillingStatus = 'NO_SUBSCRIPTION' | 'PENDING' | 'ACTIVE' | 'PAST_DUE' | 'CANCELED';

export interface BillingDto {
  provider: string;
  plan: string;
  status: BillingStatus;
  lastPaymentId?: string | null;
}

export interface PixQrDto {
  encodedImage: string;
  payload: string;
}

@Injectable({ providedIn: 'root' })
export class BillingService {
  private http = inject(HttpClient);

  getStatus() {
    return this.http.get<BillingDto>(`${environment.apiBaseUrl}/billing`);
  }

  subscribeBasic() {
    return this.http.post<BillingDto>(`${environment.apiBaseUrl}/billing/subscribe/basic`, {});
  }

  getPix() {
    return this.http.get<PixQrDto>(`${environment.apiBaseUrl}/billing/pix`);
  }
}
