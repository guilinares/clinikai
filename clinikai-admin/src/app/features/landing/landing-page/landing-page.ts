import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { OnboardingService, OnboardingRequest } from '../onboarding.service';
import { NgClass } from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-landing-page',
  imports: [FormsModule, RouterLink, NgClass],
  templateUrl: './landing-page.html',
  styleUrl: './landing-page.css',
})
export class LandingPage {
  private onboarding = inject(OnboardingService);
  private router = inject(Router);

  // Form fields
  clinicName = '';
  specialty = '';
  whatsappNumber = '';
  documento = '';
  userName = '';
  email = '';
  password = '';
  confirmPassword = '';

  loading = false;
  error: string | null = null;

  features = [
    {
      icon: '🤖',
      title: 'Atendimento com IA',
      desc: 'Automatize o primeiro contato com pacientes via WhatsApp usando inteligência artificial personalizada para sua clínica.',
    },
    {
      icon: '📅',
      title: 'Agenda Inteligente',
      desc: 'Integração com Google Calendar para agendamentos automáticos, lembretes e confirmações por WhatsApp.',
    },
    {
      icon: '💬',
      title: 'WhatsApp Integrado',
      desc: 'Gerencie todas as conversas dos pacientes em um painel centralizado com histórico completo.',
    },
    {
      icon: '📊',
      title: 'Base de Conhecimento',
      desc: 'Crie e organize informações da clínica para que a IA responda com precisão sobre procedimentos, valores e horários.',
    },
    {
      icon: '🔒',
      title: 'Seguro e Confiável',
      desc: 'Dados criptografados, autenticação robusta e infraestrutura em nuvem com alta disponibilidade.',
    },
    {
      icon: '⚡',
      title: 'Fácil de Configurar',
      desc: 'Em poucos minutos você configura o fluxo de atendimento da sua clínica e começa a atender automaticamente.',
    },
  ];

  scrollToRegister() {
    document.getElementById('cadastro')?.scrollIntoView({ behavior: 'smooth' });
  }

  submit() {
    this.error = null;

    if (this.password !== this.confirmPassword) {
      this.error = 'As senhas não coincidem.';
      return;
    }

    if (this.password.length < 6) {
      this.error = 'A senha deve ter no mínimo 6 caracteres.';
      return;
    }

    this.loading = true;

    const req: OnboardingRequest = {
      clinicName: this.clinicName,
      specialty: this.specialty,
      whatsappNumber: this.whatsappNumber,
      documento: this.documento,
      userName: this.userName,
      email: this.email,
      password: this.password,
    };

    this.onboarding.register(req).subscribe({
      next: (res) => {
        this.loading = false;
        this.router.navigate(['/cadastro-sucesso'], {
          queryParams: { clinic: res.clinicName },
        });
      },
      error: (err) => {
        this.loading = false;
        this.error =
          err.error?.message ||
          err.error?.error ||
          'Erro ao realizar cadastro. Tente novamente.';
      },
    });
  }
}
