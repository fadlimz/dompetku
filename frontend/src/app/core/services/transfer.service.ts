import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Transfer } from '../../core/models/transfer.model';

@Injectable({
  providedIn: 'root'
})
export class TransferService {
  private readonly API_URL = `${environment.apiUrl}/account-balance-transfer`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Transfer[]> {
    return this.http.get<Transfer[]>(this.API_URL);
  }

  getById(id: string): Observable<Transfer> {
    return this.http.get<Transfer>(`${this.API_URL}/${id}`);
  }

  create(transfer: Partial<Transfer>): Observable<Transfer> {
    return this.http.post<Transfer>(this.API_URL, transfer);
  }

  update(id: string, transfer: Partial<Transfer>): Observable<Transfer> {
    return this.http.put<Transfer>(`${this.API_URL}/${id}`, transfer);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
