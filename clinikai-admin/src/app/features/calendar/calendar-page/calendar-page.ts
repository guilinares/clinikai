import { Component, inject, signal, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FullCalendarComponent, FullCalendarModule } from '@fullcalendar/angular';
import { CalendarOptions, DatesSetArg, EventClickArg, EventDropArg } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin, { DateClickArg, EventResizeDoneArg } from '@fullcalendar/interaction';
import ptBrLocale from '@fullcalendar/core/locales/pt-br';
import { CalendarService } from '../../../core/api/calendar.service';

interface SelectedEvent {
  id: string;
  title: string;
  description: string | null;
  start: string;
  end: string;
}

@Component({
  standalone: true,
  imports: [FullCalendarModule, FormsModule],
  templateUrl: './calendar-page.html',
  styleUrl: './calendar-page.css',
})
export class CalendarPage {
  @ViewChild('calendarEl') calendarEl!: FullCalendarComponent;

  private calendarService = inject(CalendarService);
  private lastRange = { start: '', end: '' };

  loading = signal(false);
  error = signal<string | null>(null);
  saving = signal(false);
  canceling = signal(false);

  selectedEvent = signal<SelectedEvent | null>(null);
  editMode = signal(false);
  showCreateForm = signal(false);

  // create form fields
  newTitle = '';
  newDescription = '';
  newDate = '';
  newStartTime = '';
  newEndTime = '';

  // edit form fields
  editTitle = '';
  editDescription = '';
  editDate = '';
  editStartTime = '';
  editEndTime = '';

  calendarOptions: CalendarOptions = {
    plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
    locale: ptBrLocale,
    initialView: 'dayGridMonth',
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,timeGridWeek,timeGridDay',
    },
    height: 'auto',
    editable: true,
    eventTimeFormat: { hour: '2-digit', minute: '2-digit', hour12: false },
    eventColor: '#0c2c55',
    eventTextColor: '#ffffff',
    eventBorderColor: 'transparent',
    dayMaxEvents: 3,
    datesSet: (arg: DatesSetArg) => this.onDatesSet(arg),
    dateClick: (arg: DateClickArg) => this.onDateClick(arg),
    eventClick: (arg: EventClickArg) => this.onEventClick(arg),
    eventDrop: (arg: EventDropArg) => this.onEventDrop(arg),
    eventResize: (arg: EventResizeDoneArg) => this.onEventResize(arg),
    events: [],
  };

  private onDatesSet(arg: DatesSetArg) {
    this.lastRange = { start: arg.startStr, end: arg.endStr };
    this.loadEvents(arg.startStr, arg.endStr);
  }

  private onDateClick(arg: DateClickArg) {
    this.newTitle = '';
    this.newDescription = '';
    const { date, startTime, endTime } = this.splitDatetime(arg.dateStr, arg.dateStr);
    this.newDate = date;
    this.newStartTime = startTime;
    this.newEndTime = endTime;
    this.showCreateForm.set(true);
  }

  private onEventClick(arg: EventClickArg) {
    const e = arg.event;
    this.selectedEvent.set({
      id: e.id,
      title: e.title,
      description: e.extendedProps['description'] ?? null,
      start: e.startStr,
      end: e.endStr,
    });
  }

  private onEventDrop(arg: EventDropArg) {
    const { id, startStr, endStr } = arg.event;
    this.calendarService.rescheduleEvent(id, startStr, endStr ?? startStr).subscribe({
      error: () => {
        arg.revert();
        this.error.set('Não foi possível reagendar o evento.');
      },
    });
  }

  private onEventResize(arg: EventResizeDoneArg) {
    const { id, startStr, endStr } = arg.event;
    this.calendarService.rescheduleEvent(id, startStr, endStr ?? startStr).subscribe({
      error: () => {
        arg.revert();
        this.error.set('Não foi possível reagendar o evento.');
      },
    });
  }

  private loadEvents(start: string, end: string) {
    this.loading.set(true);
    this.error.set(null);

    this.calendarService.listEvents(start, end).subscribe({
      next: (events) => {
        this.calendarOptions = {
          ...this.calendarOptions,
          events: events.map((e) => ({
            id: e.id,
            title: e.title ?? '(sem título)',
            start: e.start,
            end: e.end,
            extendedProps: { description: e.description },
          })),
        };
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Não foi possível carregar os eventos.');
        this.loading.set(false);
      },
    });
  }

  createEvent() {
    if (!this.newTitle.trim() || !this.newDate || !this.newStartTime || !this.newEndTime) return;
    this.saving.set(true);
    this.error.set(null);

    this.calendarService.createEvent({
      title: this.newTitle.trim(),
      description: this.newDescription.trim(),
      start: new Date(`${this.newDate}T${this.newStartTime}`).toISOString(),
      end: new Date(`${this.newDate}T${this.newEndTime}`).toISOString(),
    }).subscribe({
      next: () => {
        this.saving.set(false);
        this.showCreateForm.set(false);
        this.loadEvents(this.lastRange.start, this.lastRange.end);
      },
      error: () => {
        this.saving.set(false);
        this.error.set('Não foi possível criar o evento.');
      },
    });
  }

  cancelEvent() {
    const ev = this.selectedEvent();
    if (!ev) return;
    this.canceling.set(true);

    this.calendarService.cancelEvent(ev.id).subscribe({
      next: () => {
        this.canceling.set(false);
        this.selectedEvent.set(null);
        this.loadEvents(this.lastRange.start, this.lastRange.end);
      },
      error: () => {
        this.canceling.set(false);
        this.error.set('Não foi possível cancelar o evento.');
      },
    });
  }

  openEdit() {
    const ev = this.selectedEvent();
    if (!ev) return;
    const { date, startTime, endTime } = this.splitDatetime(ev.start, ev.end);
    this.editTitle = ev.title;
    this.editDescription = ev.description ?? '';
    this.editDate = date;
    this.editStartTime = startTime;
    this.editEndTime = endTime;
    this.editMode.set(true);
  }

  saveEdit() {
    const ev = this.selectedEvent();
    if (!ev || !this.editTitle.trim() || !this.editDate || !this.editStartTime || !this.editEndTime) return;
    this.saving.set(true);
    this.error.set(null);

    this.calendarService.updateEvent(ev.id, {
      title: this.editTitle.trim(),
      description: this.editDescription.trim(),
      start: new Date(`${this.editDate}T${this.editStartTime}`).toISOString(),
      end: new Date(`${this.editDate}T${this.editEndTime}`).toISOString(),
    }).subscribe({
      next: () => {
        this.saving.set(false);
        this.editMode.set(false);
        this.selectedEvent.set(null);
        this.loadEvents(this.lastRange.start, this.lastRange.end);
      },
      error: (err) => {
        this.saving.set(false);
        this.error.set(err?.status === 409 ? 'Horário indisponível.' : 'Não foi possível salvar as alterações.');
      },
    });
  }

  closeDetail() {
    this.selectedEvent.set(null);
    this.editMode.set(false);
  }

  closeCreateForm() {
    this.showCreateForm.set(false);
  }

  formatDate(iso: string): string {
    if (!iso) return '—';
    return new Date(iso).toLocaleString('pt-BR', {
      day: '2-digit', month: '2-digit', year: 'numeric',
      hour: '2-digit', minute: '2-digit',
    });
  }

  private splitDatetime(startIso: string, endIso: string): { date: string; startTime: string; endTime: string } {
    const pad = (n: number) => String(n).padStart(2, '0');
    const hasTime = startIso.includes('T');
    const start = hasTime ? new Date(startIso) : new Date(startIso + 'T09:00:00');
    const endRaw = hasTime ? new Date(endIso) : new Date(startIso + 'T10:00:00');
    // se início == fim (clique simples), fim = início + 1h
    const end = endRaw.getTime() === start.getTime()
      ? new Date(start.getTime() + 60 * 60 * 1000)
      : endRaw;
    const date = `${start.getFullYear()}-${pad(start.getMonth() + 1)}-${pad(start.getDate())}`;
    return {
      date,
      startTime: `${pad(start.getHours())}:${pad(start.getMinutes())}`,
      endTime: `${pad(end.getHours())}:${pad(end.getMinutes())}`,
    };
  }
}
