import { Libro } from './libro.model';
import { Usuario } from './usuario.model';

export type EstadoReserva = 'PENDIENTE' | 'ATENDIDA' | 'CANCELADA';

export interface Reserva {
  id: number;
  libro: Libro;
  usuario: Usuario;
  fechaReserva: string;
  estado: EstadoReserva;
}

export interface ReservaRequest {
  libroId: number;
}
