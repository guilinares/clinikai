import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { SessionStore } from '../auth/session.store';

export interface CalendarEventDto {
  id: string;
  title: string;
  description: string | null;
  start: string;
  end: string;
}

@Injectable({ providedIn: 'root' })
export class CalendarService {
  private http = inject(HttpClient);
  private session = inject(SessionStore);

  private clinicId(): string {
    const id = this.session.clinicId;
    if (!id) throw new Error('clinicId ausente na sessão');
    return id;
  }

  listEvents(start: string, end: string) {
    const params = new HttpParams()
      .set('clinicId', this.clinicId())
      .set('start', start)
      .set('end', end);
    return this.http.get<CalendarEventDto[]>(`${environment.apiBaseUrl}/calendar/events`, { params });
  }

  createEvent(data: { title: string; description: string; start: string; end: string }) {
    return this.http.post<void>(`${environment.apiBaseUrl}/calendar/events`, {
      clinicId: this.clinicId(),
      ...data,
    });
  }

  rescheduleEvent(eventId: string, newStart: string, newEnd: string) {
    return this.http.patch<void>(`${environment.apiBaseUrl}/calendar/events/${eventId}/reschedule`, {
      clinicId: this.clinicId(),
      newStart,
      newEnd,
    });
  }

  updateEvent(eventId: string, data: { title: string; description: string; start: string; end: string }) {
    return this.http.put<void>(`${environment.apiBaseUrl}/calendar/events/${eventId}`, {
      clinicId: this.clinicId(),
      ...data,
    });
  }

  cancelEvent(eventId: string) {
    const params = new HttpParams().set('clinicId', this.clinicId());
    return this.http.delete<void>(`${environment.apiBaseUrl}/calendar/events/${eventId}`, { params });
  }
}
