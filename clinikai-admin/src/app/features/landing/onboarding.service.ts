import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export interface OnboardingRequest {
  clinicName: string;
  specialty: string;
  whatsappNumber: string;
  documento: string;
  userName: string;
  email: string;
  password: string;
}

export interface OnboardingResponse {
  clinicId: string;
  clinicName: string;
  status: string;
  message: string;
}

@Injectable({ providedIn: 'root' })
export class OnboardingService {
  private http = inject(HttpClient);

  register(data: OnboardingRequest) {
    return this.http.post<OnboardingResponse>(`${environment.apiBaseUrl}/onboarding`, data);
  }
}
