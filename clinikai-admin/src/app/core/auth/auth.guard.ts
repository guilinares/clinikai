import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { SessionStore } from './session.store';
import { AuthService } from './auth.service';
import { catchError, map, of, tap } from 'rxjs';

export const AuthGuard: CanActivateFn = () => {
  const session = inject(SessionStore);
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!session.token) {
    router.navigateByUrl('/admin/login');
    return false;
  }

  return auth.me().pipe(
    tap((user) => {
      const s = session.get();
      if (s) {
        session.set({
          ...s,
          clinicId: user.clinicId ?? s.clinicId,
          user: {
            id: user.id,
            name: user.name,
            email: user.email,
            role: user.role,
          },
        });
      }
    }),
    map(() => true),
    catchError(() => {
      session.clear();
      router.navigateByUrl('/admin/login');
      return of(false);
    })
  );
};
