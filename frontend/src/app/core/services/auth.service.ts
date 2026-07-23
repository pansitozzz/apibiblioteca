import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RegistroRequest } from '../models/auth.model';
import { Usuario } from '../models/usuario.model';

const TOKEN_KEY = 'biblioteca_token';
const USUARIO_KEY = 'biblioteca_usuario';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly usuarioActual = signal<Usuario | null>(this.leerUsuarioGuardado());

  readonly usuario = computed(() => this.usuarioActual());
  readonly estaAutenticado = computed(() => this.usuarioActual() !== null);
  readonly rol = computed(() => this.usuarioActual()?.rol ?? null);

  constructor(private readonly http: HttpClient) {}

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/login`, request).pipe(
      tap((response) => this.guardarSesion(response))
    );
  }

  registrar(request: RegistroRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/register`, request).pipe(
      tap((response) => this.guardarSesion(response))
    );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USUARIO_KEY);
    this.usuarioActual.set(null);
  }

  obtenerToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  private guardarSesion(response: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, response.token);
    localStorage.setItem(USUARIO_KEY, JSON.stringify(response.usuario));
    this.usuarioActual.set(response.usuario);
  }

  private leerUsuarioGuardado(): Usuario | null {
    const raw = localStorage.getItem(USUARIO_KEY);
    return raw ? (JSON.parse(raw) as Usuario) : null;
  }
}
