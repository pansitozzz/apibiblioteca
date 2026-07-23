import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Prestamo, PrestamoRequest } from '../models/prestamo.model';
import { PageResponse } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class PrestamoService {
  private readonly baseUrl = `${environment.apiUrl}/prestamos`;

  constructor(private readonly http: HttpClient) {}

  listar(page = 0, size = 10): Observable<PageResponse<Prestamo>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Prestamo>>(this.baseUrl, { params });
  }

  crear(request: PrestamoRequest): Observable<Prestamo> {
    return this.http.post<Prestamo>(this.baseUrl, request);
  }

  devolver(id: number): Observable<Prestamo> {
    return this.http.put<Prestamo>(`${this.baseUrl}/${id}/devolver`, {});
  }
}
