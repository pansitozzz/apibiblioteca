import { Libro } from './libro.model';
import { Usuario } from './usuario.model';

export type EstadoPrestamo = 'ACTIVO' | 'DEVUELTO' | 'ATRASADO';

export interface Prestamo {
  id: number;
  libro: Libro;
  usuario: Usuario;
  fechaPrestamo: string;
  fechaDevolucionEsperada: string;
  fechaDevolucionReal: string | null;
  estado: EstadoPrestamo;
}

export interface PrestamoRequest {
  libroId: number;
  usuarioId: number | null;
}
