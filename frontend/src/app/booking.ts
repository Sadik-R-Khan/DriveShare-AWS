import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { timeout } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class BookingService {
  private api = `${environment.apiUrl}/bookings`;
  constructor(private http: HttpClient) {}

  create(data: any) {
    return this.http.post<any>(this.api, data).pipe(timeout(10000));
  }
  getMine() {
    return this.http.get<any[]>(`${this.api}/my`).pipe(timeout(10000));
  }
  getByRenter(renterId: number) {
    return this.http.get<any[]>(`${this.api}/renter/${renterId}`).pipe(timeout(10000));
  }
  cancel(id: number) {
    return this.http.put(`${this.api}/${id}/cancel`, {}).pipe(timeout(10000));
  }
  updateStatus(id: number, status: string) {
    return this.http.put<any>(`${this.api}/${id}/status?status=${status}`, {}).pipe(timeout(10000));
  }
}
