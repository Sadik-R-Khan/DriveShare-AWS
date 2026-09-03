import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap, timeout } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class BookingService {
  private api = `${environment.apiUrl}/bookings`;

  myBookings = signal<any[]>([]);
  renterBookings = signal<any[]>([]);

  constructor(private http: HttpClient) {}

  loadMine() {
    this.http.get<any[]>(`${this.api}/my`).pipe(timeout(10000)).subscribe({
      next: b => this.myBookings.set(b),
      error: () => {},
    });
  }

  loadByRenter(renterId: number) {
    this.http.get<any[]>(`${this.api}/renter/${renterId}`).pipe(timeout(10000)).subscribe({
      next: b => this.renterBookings.set(b),
      error: () => {},
    });
  }

  create(data: any) {
    return this.http.post<any>(this.api, data).pipe(
      timeout(10000),
      tap(b => this.myBookings.update(list => [...list, b]))
    );
  }

  getMine() {
    return this.http.get<any[]>(`${this.api}/my`).pipe(timeout(10000));
  }

  getByRenter(renterId: number) {
    return this.http.get<any[]>(`${this.api}/renter/${renterId}`).pipe(timeout(10000));
  }

  cancel(id: number) {
    return this.http.put(`${this.api}/${id}/cancel`, {}).pipe(
      timeout(10000),
      tap(() => this.myBookings.update(list =>
        list.map(b => b.id === id ? { ...b, status: 'CANCELLED' } : b)
      ))
    );
  }

  updateStatus(id: number, status: string) {
    return this.http.put<any>(`${this.api}/${id}/status?status=${status}`, {}).pipe(
      timeout(10000),
      tap(updated => this.renterBookings.update(list =>
        list.map(b => b.id === id ? updated : b)
      ))
    );
  }
}
