import { Injectable } from '@angular/core';

export interface Session {
  accessToken: string;
  clinicId: string;
  user: {
    id: string;
    name: string;
    email: string;
    role: string;
  };
}

const KEY = 'clinikai.admin.session';

@Injectable({ providedIn: 'root' })
export class SessionStore {
  get(): Session | null {
    const raw = localStorage.getItem(KEY);
    return raw ? (JSON.parse(raw) as Session) : null;
  }

  set(session: Session) {
    localStorage.setItem(KEY, JSON.stringify(session));
  }

  clear() {
    localStorage.removeItem(KEY);
  }

  get token(): string | null {
    return this.get()?.accessToken ?? null;
  }

  get clinicId(): string | null {
    return this.get()?.clinicId ?? null;
  }

  get userName(): string | null {
    return this.get()?.user?.name ?? null;
  }

  get role(): string | null {
    return this.get()?.user?.role ?? null;
  }
}
