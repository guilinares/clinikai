import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { SessionStore } from './session.store';
import { AuthUser, LoginResponse } from './auth.models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private session = inject(SessionStore);

  login(email: string, password: string) {
    return this.http
      .post<LoginResponse>(`${environment.apiBaseUrl}/auth/login`, { email, password })
      .pipe(
        tap((res) => {
          this.session.set({
            accessToken: res.accessToken,
            clinicId: res.clinicId,
            user: {
              id: res.user.id,
              name: res.user.name,
              email: res.user.email,
              role: res.user.role,
            },
          });
        })
      );
  }

  me() {
    return this.http.get<AuthUser>(`${environment.apiBaseUrl}/me`);
  }

  logout() {
    this.session.clear();
  }
}
