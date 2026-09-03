import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class CarService {
  private api = `${environment.apiUrl}/cars`;

  cars = signal<any[]>([]);
  renterCars = signal<any[]>([]);

  constructor(private http: HttpClient) {}

  loadAll() {
    this.http.get<any[]>(this.api).subscribe(c => this.cars.set(c));
  }

  getById(id: number) {
    return this.http.get<any>(`${this.api}/${id}`);
  }

  search(q: string) {
    return this.http.get<any[]>(`${this.api}/search?query=${encodeURIComponent(q)}`).pipe(
      tap(c => this.cars.set(c))
    );
  }

  loadByRenter(renterId: number) {
    this.http.get<any[]>(`${this.api}/renter/${renterId}`).subscribe(c => this.renterCars.set(c));
  }

  create(car: any) {
    return this.http.post<any>(this.api, car).pipe(
      tap(c => {
        this.renterCars.update(list => [...list, c]);
        this.cars.update(list => [...list, c]);
      })
    );
  }

  delete(id: number) {
    return this.http.delete(`${this.api}/${id}`).pipe(
      tap(() => {
        this.renterCars.update(list => list.filter(c => c.id !== id));
        this.cars.update(list => list.filter(c => c.id !== id));
      })
    );
  }
}
