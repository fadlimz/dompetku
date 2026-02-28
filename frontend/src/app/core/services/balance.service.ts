import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AccountBalance } from '../models/balance.model';

@Injectable({
  providedIn: 'root'
})
export class BalanceService {
  private readonly API_URL = `${environment.apiUrl}/balances`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<AccountBalance[]> {
    return this.http.get<AccountBalance[]>(this.API_URL);
  }
}
