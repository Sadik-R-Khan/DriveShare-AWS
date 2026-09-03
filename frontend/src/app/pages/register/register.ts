import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../auth';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './register.html',
})
export class RegisterComponent {
  name = '';
  email = '';
  password = '';
  phone = '';
  role: 'USER' | 'RENTER' = 'USER';
  error = '';

  constructor(
    private auth: AuthService,
    private router: Router,
  ) {}

  register() {
    this.error = '';
    this.auth
      .register({
        name: this.name,
        email: this.email,
        password: this.password,
        phone: this.phone,
        role: this.role,
      })
      .subscribe({
        next: (u) => this.router.navigate([u.role === 'RENTER' ? '/renter' : '/bookings']),
        error: (err) => (this.error = err.error?.message || 'Registration failed'),
      });
  }
}
