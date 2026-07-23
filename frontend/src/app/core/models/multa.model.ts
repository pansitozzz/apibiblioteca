import { Prestamo } from './prestamo.model';

export interface Multa {
  id: number;
  prestamo: Prestamo;
  monto: number;
  pagada: boolean;
  fechaGeneracion: string;
}
