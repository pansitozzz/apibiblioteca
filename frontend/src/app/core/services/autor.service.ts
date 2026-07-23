import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Autor, AutorRequest } from '../models/autor.model';

@Injectable({ providedIn: 'root' })
export class AutorService {
  private readonly baseUrl = `${environment.apiUrl}/autores`;

  constructor(private readonly http: HttpClient) {}

  listar(): Observable<Autor[]> {
    return this.http.get<Autor[]>(this.baseUrl);
  }

  obtenerPorId(id: number): Observable<Autor> {
    return this.http.get<Autor>(`${this.baseUrl}/${id}`);
  }

  crear(request: AutorRequest): Observable<Autor> {
    return this.http.post<Autor>(this.baseUrl, request);
  }

  actualizar(id: number, request: AutorRequest): Observable<Autor> {
    return this.http.put<Autor>(`${this.baseUrl}/${id}`, request);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
