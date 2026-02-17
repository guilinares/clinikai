import { ApplicationConfig } from '@angular/core';
import { provideZonelessChangeDetection } from '@angular/core';
import { provideRouter, RouteReuseStrategy } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { authInterceptor } from './core/api/auth.interceptor';
import { FlowReuseStrategy } from './core/routing/flow-reuse.strategy';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZonelessChangeDetection(),
    provideRouter(routes),
    { provide: RouteReuseStrategy, useClass: FlowReuseStrategy },
    provideHttpClient(withInterceptors([authInterceptor])),
  ],
};
