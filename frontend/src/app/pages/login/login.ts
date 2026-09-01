import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './login.html',
})
export class LoginComponent {
  email = '';
  password = '';
  error = '';

  constructor(
    private auth: AuthService,
    private router: Router,
  ) {}

  login() {
    this.error = '';
    this.auth.login(this.email, this.password).subscribe({
      next: (u) => this.router.navigate([u.role === 'RENTER' ? '/renter' : '/bookings']),
      error: () => (this.error = 'Invalid email or password'),
    });
  }
}
