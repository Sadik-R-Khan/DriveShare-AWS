import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, tap } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private api = environment.apiUrl;
  private userSubject = new BehaviorSubject<any>(
    JSON.parse(localStorage.getItem('user') || 'null')
  );
  user$ = this.userSubject.asObservable();

  constructor(private http: HttpClient) {}

  get user() { return this.userSubject.value; }
  get isLoggedIn() { return !!this.user; }
  get isRenter() { return this.user?.role === 'RENTER'; }

  login(email: string, password: string) {
    return this.http.post<any>(`${this.api}/auth/login`, { email, password }).pipe(
      tap(u => { localStorage.setItem('user', JSON.stringify(u)); this.userSubject.next(u); })
    );
  }

  register(data: any) {
    return this.http.post<any>(`${this.api}/auth/register`, data).pipe(
      tap(u => { localStorage.setItem('user', JSON.stringify(u)); this.userSubject.next(u); })
    );
  }

  logout() {
    localStorage.removeItem('user');
    this.userSubject.next(null);
  }
}