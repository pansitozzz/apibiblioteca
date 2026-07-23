import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

/**
 * Maneja de forma centralizada los errores de autenticación/autorización:
 * - 401: la sesión expiró o el token es inválido -> se cierra sesión y se redirige al login.
 * - 403: el usuario no tiene permisos para la acción -> se redirige a una página de acceso denegado.
 * El resto de errores se propagan para que cada componente los maneje (ej. mostrar un mensaje).
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        authService.logout();
        router.navigate(['/login']);
      } else if (error.status === 403) {
        router.navigate(['/acceso-denegado']);
      }
      return throwError(() => error);
    })
  );
};
