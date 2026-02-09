import { throwError } from 'rxjs';
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { SessionStore } from '../../core/auth/session.store';
import { ClinicKbEntry, PagedResponse, CreateKbRequest } from './kb.models';

@Injectable({ providedIn: 'root' })
export class KbService {
  private http = inject(HttpClient);
  private session = inject(SessionStore);

  private clinicId(): string {
    const id = this.session.clinicId;
    if (!id) throw new Error('clinicId ausente na sessão');
    return id;
  }

  list(params: {
    page?: number;
    size?: number;
    sort?: string;      // "updatedAt,desc"
    enabled?: boolean;
    category?: string;
    tag?: string;
    q?: string;         // se você quiser usar o mesmo endpoint pra buscar
  }) {
    let p = new HttpParams();
    for (const [k, v] of Object.entries(params)) {
      if (v !== undefined && v !== null && v !== '') p = p.set(k, String(v));
    }

    return this.http.get<PagedResponse<ClinicKbEntry>>(
      `${environment.apiBaseUrl}/clinics/${this.clinicId()}/kb`,
      { params: p }
    );
  }

  // Se seu backend tem esse endpoint separado:
  search(q: string, limit = 10, page = 0) {
    const params = new HttpParams()
      .set('q', q)
      .set('limit', String(limit))
      .set('page', String(page));

    return this.http.get<PagedResponse<ClinicKbEntry>>(
      `${environment.apiBaseUrl}/clinics/${this.clinicId()}/kb/search`,
      { params }
    );
  }

  create(body: CreateKbRequest) {
    return this.http.post<ClinicKbEntry>(
      `${environment.apiBaseUrl}/clinics/${this.clinicId()}/kb`,
      body
    );
  }

  setEnabled(id: string, enabled: boolean) {

    const params = new HttpParams()
      .set('enabled', enabled);
    return this.http.patch(
      `${environment.apiBaseUrl}/clinics/kb/${id}/enabled`,
      null,
      { params: params }
    );
  }

  delete(id: string) {
    return this.http.delete(`${environment.apiBaseUrl}/clinics/kb/${id}`);
  }

  getById(id: string) {
    return this.http.get<ClinicKbEntry>(
      `${environment.apiBaseUrl}/clinics/kb/${id}`
    );
  }

  update(id: string, body: CreateKbRequest) {
    return this.http.put<ClinicKbEntry>(
      `${environment.apiBaseUrl}/clinics/${this.clinicId()}/kb/${id}`,
      body
    );
  }
}
