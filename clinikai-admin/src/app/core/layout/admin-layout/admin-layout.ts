import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { SessionStore } from '../../auth/session.store';
import { AuthService } from '../../auth/auth.service';


@Component({
  standalone: true,
  selector: 'app-admin-layout',
  imports: [RouterOutlet, RouterLink],
  templateUrl: './admin-layout.html',
  styleUrl: './admin-layout.css',
})
export class AdminLayout {
  private router = inject(Router);
  session = inject(SessionStore);
  auth = inject(AuthService);

  logout() {
    this.auth.logout();
    this.router.navigateByUrl('/admin/login');
  }
}
