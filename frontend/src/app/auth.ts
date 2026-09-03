import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private api = environment.apiUrl;

  user = signal<any>(JSON.parse(localStorage.getItem('user') || 'null'));
  isLoggedIn = computed(() => !!this.user());
  isRenter = computed(() => this.user()?.role === 'RENTER');

  constructor(private http: HttpClient) {}

  login(email: string, password: string) {
    return this.http.post<any>(`${this.api}/auth/login`, { email, password }).pipe(
      tap(u => { localStorage.setItem('user', JSON.stringify(u)); this.user.set(u); })
    );
  }

  register(data: any) {
    return this.http.post<any>(`${this.api}/auth/register`, data).pipe(
      tap(u => { localStorage.setItem('user', JSON.stringify(u)); this.user.set(u); })
    );
  }

  logout() {
    localStorage.removeItem('user');
    this.user.set(null);
  }
}