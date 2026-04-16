import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-register-success',
  imports: [RouterLink],
  templateUrl: './register-success.html',
  styleUrl: './register-success.css',
})
export class RegisterSuccess implements OnInit {
  private route = inject(ActivatedRoute);

  clinicName = '';

  ngOnInit() {
    this.clinicName = this.route.snapshot.queryParamMap.get('clinic') ?? 'Sua clínica';
  }
}
