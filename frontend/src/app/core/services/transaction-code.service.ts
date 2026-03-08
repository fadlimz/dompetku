import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface TransactionCode {
  id: string;
  transactionCode: string;
  transactionName: string;
  activeFlag: string;
}

@Injectable({
  providedIn: 'root'
})
export class TransactionCodeService {
  private readonly API_URL = `${environment.apiUrl}/transaction-codes`;

  constructor(private http: HttpClient) {}

  getAll(keyword?: string): Observable<TransactionCode[]> {
    const params = keyword ? { keyword } : undefined;
    return this.http.get<TransactionCode[]>(this.API_URL, { params });
  }

  getById(id: string): Observable<TransactionCode> {
    return this.http.get<TransactionCode>(`${this.API_URL}/${id}`);
  }
}
