import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Rol } from '../models/usuario.model';

/**
 * Crea un guard que exige que el usuario autenticado tenga alguno de los
 * roles indicados. Se usa en la configuracion de rutas, ej:
 * canActivate: [roleGuard(['ADMIN', 'BIBLIOTECARIO'])]
 */
export function roleGuard(rolesPermitidos: Rol[]): CanActivateFn {
  return () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (!authService.estaAutenticado()) {
      router.navigate(['/login']);
      return false;
    }

    const rolActual = authService.rol();
    if (rolActual && rolesPermitidos.includes(rolActual)) {
      return true;
    }

    router.navigate(['/acceso-denegado']);
    return false;
  };
}
