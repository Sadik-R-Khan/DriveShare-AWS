import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class RenterService {
  private api = `${environment.apiUrl}/renters`;
  constructor(private http: HttpClient) {}

  createProfile(data: any) { return this.http.post<any>(this.api, data); }
  getMyProfile() { return this.http.get<any>(`${this.api}/me`); }
}