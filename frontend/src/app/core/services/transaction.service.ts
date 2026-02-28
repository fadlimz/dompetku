import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DailyCash } from '../../core/models/transaction.model';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private readonly API_URL = `${environment.apiUrl}/daily-cash`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<DailyCash[]> {
    return this.http.get<DailyCash[]>(this.API_URL);
  }

  getById(id: string): Observable<DailyCash> {
    return this.http.get<DailyCash>(`${this.API_URL}/${id}`);
  }

  create(transaction: Partial<DailyCash>): Observable<DailyCash> {
    return this.http.post<DailyCash>(this.API_URL, transaction);
  }

  update(id: string, transaction: Partial<DailyCash>): Observable<DailyCash> {
    return this.http.put<DailyCash>(`${this.API_URL}/${id}`, transaction);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
