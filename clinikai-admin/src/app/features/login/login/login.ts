import { Component, inject, OnInit, signal } from '@angular/core';
import { AuthService } from '../../../core/auth/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  standalone: true,
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login implements OnInit {
  private auth = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  email = signal('');
  password = signal('');
  loading = signal(false);
  error = signal<string | null>(null);
  warning = signal<string | null>(null);

  ngOnInit() {
    if (this.route.snapshot.queryParamMap.get('sessionExpired')) {
      this.warning.set('Sua sessão expirou. Faça login novamente.');
    }
  }

  submit() {
    this.error.set(null);
    this.loading.set(true);

    this.auth.login(this.email(), this.password()).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigateByUrl('/admin');
      },
      error: (err) => {
        this.loading.set(false);
        console.error('Login error:', err);

        let body: any = err?.error;

        if (typeof err?.error === 'string') {
          try {
            body = JSON.parse(err.error);
          } catch {
            body = null;
          }
        }

        this.error.set(body?.message || 'Falha no login. Verifique suas credenciais.');
      },
    });
  }
}
