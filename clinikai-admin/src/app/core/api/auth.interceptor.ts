import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { SessionStore } from '../auth/session.store';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const session = inject(SessionStore);
  const router = inject(Router);

  const token = session.token;

  const authReq =
    token && !req.url.includes('/auth/login')
      ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
      : req;

  return next(authReq).pipe(
    catchError((err) => {
      if (err?.status === 401) {
        session.clear();
        router.navigateByUrl('/admin/login');
      }
      if (err.status === 403 && err?.error?.code === 'BILLING_INACTIVE') {
        router.navigateByUrl('/admin/settings/billing');
      }
      return throwError(() => err);
    })
  );
};
