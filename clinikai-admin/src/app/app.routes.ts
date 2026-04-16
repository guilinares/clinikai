import { Routes } from '@angular/router';

import { AdminLayout } from './core/layout/admin-layout/admin-layout';
import { Login } from './features/login/login/login';
import { AuthGuard } from './core/auth/auth.guard';
import { KbList } from './features/kb/kb-list/kb-list';
import { KbForm } from './features/kb/kb-form/kb-form';
import { LandingPage } from './features/landing/landing-page/landing-page';
import { RegisterSuccess } from './features/landing/register-success/register-success';

export const routes: Routes = [
  // Rotas públicas
  { path: '', component: LandingPage },
  { path: 'cadastro-sucesso', component: RegisterSuccess },
  { path: 'admin/login', component: Login },

  // Rotas protegidas
  {
    path: 'admin',
    component: AdminLayout,
    canActivate: [AuthGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'kb' },
      { path: 'kb', component: KbList },
      { path: 'kb/new', component: KbForm },
      { path: 'kb/:id/edit', component: KbForm },
      {
        path: 'home',
        loadComponent: () =>
          import('./features/auth/placeholder/placeholder').then(m => m.PlaceholderComponent),
      },
      { path: 'flow', loadComponent: () => import('./features/flow-embed/flow-embed').then(m => m.FlowEmbed) },
      { path: 'calendar', loadComponent: () => import('./features/calendar/calendar-page/calendar-page').then(m => m.CalendarPage) },
      {
        path: 'settings',
        loadComponent: () => import('./features/settings/settings-shell/settings-shell').then(m => m.SettingsShell),
        children: [
          { path: '', pathMatch: 'full', redirectTo: 'billing' },
          { path: 'billing', loadComponent: () => import('./features/settings/billing-page/billing-page').then(m => m.BillingPage) },
          { path: 'google', loadComponent: () => import('./features/settings/google-page/google-page').then(m => m.GoogleSettingsPage) },
          { path: 'whatsapp', loadComponent: () => import('./features/settings/whatsapp-settings/whatsapp-settings').then(m => m.WhatsappSettingsComponent) },
        ]
      },
    ],
  },
  { path: '**', redirectTo: '' },
];
