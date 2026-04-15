import { Component, inject, OnInit } from '@angular/core';
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

  email = '';
  password = '';
  loading = false;
  error: string | null = null;
  warning: string | null = null;

  ngOnInit() {
    if (this.route.snapshot.queryParamMap.get('sessionExpired')) {
      this.warning = 'Sua sessão expirou. Faça login novamente.';
    }
  }

  submit() {
    this.error = null;
    this.loading = true;

    this.auth.login(this.email, this.password).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigateByUrl('/admin');
      },
      error: () => {
        this.loading = false;
        this.error = 'Falha no login. Verifique suas credenciais.';
      },
    });
  }
}
