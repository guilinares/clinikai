import { Routes } from '@angular/router';

import { AdminLayout } from './core/layout/admin-layout/admin-layout';
import { Login } from './features/login/login/login';
import { AuthGuard } from './core/auth/auth.guard';
import { KbList } from './features/kb/kb-list/kb-list';
import { KbForm } from './features/kb/kb-form/kb-form';

export const routes: Routes = [
  { path: 'admin/login', component: Login },

  {
    path: 'admin',
    component: AdminLayout,
    canActivate: [AuthGuard],
    children: [
      // por enquanto não temos KB ainda — vai cair em uma tela placeholder
      { path: '', pathMatch: 'full', redirectTo: 'kb' },
      { path: 'kb', component: KbList },
      { path: 'kb/new', component: KbForm },
      { path: 'kb/:id/edit', component: KbForm }, 
      {
        path: 'home',
        loadComponent: () =>
          import('./features/auth/placeholder/placeholder').then(m => m.PlaceholderComponent),
      },
    ],
  },

  { path: '', pathMatch: 'full', redirectTo: 'admin' },
  { path: '**', redirectTo: 'admin' },
];
