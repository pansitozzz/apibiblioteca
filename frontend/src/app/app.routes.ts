import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'catalogo' },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then((m) => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register.component').then((m) => m.RegisterComponent)
  },
  {
    path: 'catalogo',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/catalogo/catalogo-list/catalogo-list.component').then((m) => m.CatalogoListComponent)
  },
  {
    path: 'catalogo/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/catalogo/libro-detalle/libro-detalle.component').then((m) => m.LibroDetalleComponent)
  },
  {
    path: 'mis-prestamos',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/prestamos/mis-prestamos/mis-prestamos.component').then((m) => m.MisPrestamosComponent)
  },
  {
    path: 'admin/libros',
    canActivate: [roleGuard(['ADMIN', 'BIBLIOTECARIO'])],
    loadComponent: () =>
      import('./features/admin/libros-admin/libros-admin.component').then((m) => m.LibrosAdminComponent)
  },
  {
    path: 'admin/autores',
    canActivate: [roleGuard(['ADMIN', 'BIBLIOTECARIO'])],
    loadComponent: () =>
      import('./features/admin/autores-admin/autores-admin.component').then((m) => m.AutoresAdminComponent)
  },
  {
    path: 'admin/categorias',
    canActivate: [roleGuard(['ADMIN', 'BIBLIOTECARIO'])],
    loadComponent: () =>
      import('./features/admin/categorias-admin/categorias-admin.component').then((m) => m.CategoriasAdminComponent)
  },
  {
    path: 'admin/prestamos',
    canActivate: [roleGuard(['ADMIN', 'BIBLIOTECARIO'])],
    loadComponent: () =>
      import('./features/admin/prestamos-admin/prestamos-admin.component').then((m) => m.PrestamosAdminComponent)
  },
  {
    path: 'acceso-denegado',
    loadComponent: () =>
      import('./shared/acceso-denegado/acceso-denegado.component').then((m) => m.AccesoDenegadoComponent)
  },
  { path: '**', redirectTo: 'catalogo' }
];
