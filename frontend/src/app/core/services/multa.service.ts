import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Multa } from '../models/multa.model';
import { PageResponse } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class MultaService {
  private readonly baseUrl = `${environment.apiUrl}/multas`;

  constructor(private readonly http: HttpClient) {}

  listar(page = 0, size = 10): Observable<PageResponse<Multa>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Multa>>(this.baseUrl, { params });
  }

  pagar(id: number): Observable<Multa> {
    return this.http.put<Multa>(`${this.baseUrl}/${id}/pagar`, {});
  }
}
