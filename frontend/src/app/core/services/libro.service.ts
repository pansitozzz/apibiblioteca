import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Libro, LibroRequest } from '../models/libro.model';
import { PageResponse } from '../models/page.model';

export interface LibroFiltro {
  titulo?: string;
  autor?: string;
  categoriaId?: number;
  page?: number;
  size?: number;
}

@Injectable({ providedIn: 'root' })
export class LibroService {
  private readonly baseUrl = `${environment.apiUrl}/libros`;

  constructor(private readonly http: HttpClient) {}

  buscar(filtro: LibroFiltro): Observable<PageResponse<Libro>> {
    let params = new HttpParams()
      .set('page', filtro.page ?? 0)
      .set('size', filtro.size ?? 10);

    if (filtro.titulo) params = params.set('titulo', filtro.titulo);
    if (filtro.autor) params = params.set('autor', filtro.autor);
    if (filtro.categoriaId) params = params.set('categoriaId', filtro.categoriaId);

    return this.http.get<PageResponse<Libro>>(this.baseUrl, { params });
  }

  obtenerPorId(id: number): Observable<Libro> {
    return this.http.get<Libro>(`${this.baseUrl}/${id}`);
  }

  crear(request: LibroRequest): Observable<Libro> {
    return this.http.post<Libro>(this.baseUrl, request);
  }

  actualizar(id: number, request: LibroRequest): Observable<Libro> {
    return this.http.put<Libro>(`${this.baseUrl}/${id}`, request);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
