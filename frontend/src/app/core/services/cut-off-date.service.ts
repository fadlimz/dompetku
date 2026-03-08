import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CutOffDate } from '../models/cut-off-date.model';

@Injectable({
  providedIn: 'root'
})
export class CutOffDateService {
  private readonly API_URL = `${environment.apiUrl}/cut-off-dates`;

  constructor(private http: HttpClient) {}

  getAll(keyword?: string): Observable<CutOffDate[]> {
    const params = keyword ? { keyword } : undefined;
    return this.http.get<CutOffDate[]>(this.API_URL, { params });
  }

  getById(id: string): Observable<CutOffDate> {
    return this.http.get<CutOffDate>(`${this.API_URL}/${id}`);
  }

  create(cutOffDate: Partial<CutOffDate>): Observable<CutOffDate> {
    return this.http.post<CutOffDate>(this.API_URL, cutOffDate);
  }

  update(id: string, cutOffDate: Partial<CutOffDate>): Observable<CutOffDate> {
    return this.http.put<CutOffDate>(`${this.API_URL}/${id}`, cutOffDate);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
