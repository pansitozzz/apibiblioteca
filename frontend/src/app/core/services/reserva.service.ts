import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Reserva, ReservaRequest } from '../models/reserva.model';
import { PageResponse } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class ReservaService {
  private readonly baseUrl = `${environment.apiUrl}/reservas`;

  constructor(private readonly http: HttpClient) {}

  listar(page = 0, size = 10): Observable<PageResponse<Reserva>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Reserva>>(this.baseUrl, { params });
  }

  crear(request: ReservaRequest): Observable<Reserva> {
    return this.http.post<Reserva>(this.baseUrl, request);
  }

  cancelar(id: number): Observable<Reserva> {
    return this.http.put<Reserva>(`${this.baseUrl}/${id}/cancelar`, {});
  }
}
