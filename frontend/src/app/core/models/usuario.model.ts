export type Rol = 'ADMIN' | 'BIBLIOTECARIO' | 'ESTUDIANTE';

export interface Usuario {
  id: number;
  nombre: string;
  apellido: string;
  email: string;
  rol: Rol;
  codigoEstudiante: string | null;
}

export interface UsuarioUpdateRequest {
  nombre: string;
  apellido: string;
  codigoEstudiante: string | null;
}
