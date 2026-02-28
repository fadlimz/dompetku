import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Category } from '../../core/models/category.model';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private readonly API_URL = `${environment.apiUrl}/categories`;

  constructor(private http: HttpClient) {}

  getAll(keyword?: string): Observable<Category[]> {
    const params = keyword ? { keyword } : {};
    return this.http.get<Category[]>(this.API_URL, { params });
  }

  getById(id: string): Observable<Category> {
    return this.http.get<Category>(`${this.API_URL}/${id}`);
  }

  create(category: Partial<Category>): Observable<Category> {
    return this.http.post<Category>(this.API_URL, category);
  }

  update(id: string, category: Partial<Category>): Observable<Category> {
    return this.http.put<Category>(`${this.API_URL}/${id}`, category);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
