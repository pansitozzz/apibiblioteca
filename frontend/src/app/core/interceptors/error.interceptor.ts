import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

/**
 * Maneja de forma centralizada los errores de autenticacion/autorizacion:
 * - 401: la sesion expiro o el token es invalido -> se cierra sesion y se redirige al login.
 * - 403: el usuario no tiene permisos para la accion -> se redirige a una pagina de acceso denegado.
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
