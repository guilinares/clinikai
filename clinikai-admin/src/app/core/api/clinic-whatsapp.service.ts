import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type ClinicWhatsappProvider = 'ZAPI';

export type ClinicWhatsappStatus =
  | 'UNPROVISIONED'
  | 'PROVISIONING'
  | 'AWAITING_QR'
  | 'CONNECTED'
  | 'DISCONNECTED'
  | 'SUSPENDED'
  | 'ERROR';

export interface ClinicWhatsappStatusResponse {
  status: ClinicWhatsappStatus;
  connected: boolean;
  session: boolean;
  smartphoneConnected: boolean;
  created?: number | null;      // epoch ms
  message?: string | null;
  lastErrorCode?: string | null;
  lastErrorMessage?: string | null;
}

export interface ClinicWhatsappQrResponse {
  status: ClinicWhatsappStatus;
  qrBase64: string;              // "data:image/png;base64,..."
  expiresInSeconds?: number;     // opcional
}

export interface ApiErrorResponse {
  code: string;
  message: string;
  action?: string | null;
  timestamp?: string;
}

@Injectable({ providedIn: 'root' })
export class WhatsappSettingsService {
  private readonly baseUrl = '/api';

  constructor(private http: HttpClient) {}

  getStatus(): Observable<ClinicWhatsappStatusResponse> {
    return this.http.get<ClinicWhatsappStatusResponse>(
      `${this.baseUrl}/whatsapp/status`
    );
  }

  getQr(): Observable<ClinicWhatsappQrResponse> {
    return this.http.get<ClinicWhatsappQrResponse>(
      `${this.baseUrl}/whatsapp/qr`
    );
  }

  static parseApiError(err: any): ApiErrorResponse | null {
    const body = err?.error;
    if (body && typeof body === 'object' && typeof body.code === 'string') return body as ApiErrorResponse;
    return null;
  }
}
