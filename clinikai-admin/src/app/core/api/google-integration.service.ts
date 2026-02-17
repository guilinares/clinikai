import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export interface GoogleStatusDto {
  connected: boolean;
  email?: string | null;
}

@Injectable({ providedIn: 'root' })
export class GoogleIntegrationService {
  private http = inject(HttpClient);

  status() {
    return this.http.get<GoogleStatusDto>(
      `${environment.apiBaseUrl}/integrations/google/status`
    );
  }

  getAuthorizeUrl() {
    return this.http.get<{ url: string }>(
      `${environment.apiBaseUrl}/integrations/google/authorize-url`,
    );
  }

  disconnect() {
    return this.http.post(
      `${environment.apiBaseUrl}/integrations/google/disconnect`,
      {}
    );
  }
}
