import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class CarService {
  private api = `${environment.apiUrl}/cars`;
  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<any[]>(this.api);
  }
  getById(id: number) {
    return this.http.get<any>(`${this.api}/${id}`);
  }
  search(q: string) {
    return this.http.get<any[]>(`${this.api}/search?query=${encodeURIComponent(q)}`);
  }
  getByRenter(renterId: number) {
    return this.http.get<any[]>(`${this.api}/renter/${renterId}`);
  }
  create(car: any) {
    return this.http.post<any>(this.api, car);
  }
  delete(id: number) {
    return this.http.delete(`${this.api}/${id}`);
  }
}
