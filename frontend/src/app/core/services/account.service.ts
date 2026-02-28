import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Account } from '../../core/models/account.model';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private readonly API_URL = `${environment.apiUrl}/accounts`;

  constructor(private http: HttpClient) {}

  getAll(keyword?: string): Observable<Account[]> {
    if (keyword) {
      return this.http.get<Account[]>(this.API_URL, { params: { keyword } });
    }
    return this.http.get<Account[]>(this.API_URL);
  }

  getById(id: string): Observable<Account> {
    return this.http.get<Account>(`${this.API_URL}/${id}`);
  }

  create(account: Partial<Account>): Observable<Account> {
    return this.http.post<Account>(this.API_URL, account);
  }

  update(id: string, account: Partial<Account>): Observable<Account> {
    return this.http.put<Account>(`${this.API_URL}/${id}`, account);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
