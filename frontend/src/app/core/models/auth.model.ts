import { Usuario } from './usuario.model';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegistroRequest {
  nombre: string;
  apellido: string;
  email: string;
  password: string;
  codigoEstudiante: string | null;
}

export interface AuthResponse {
  token: string;
  tipo: string;
  usuario: Usuario;
}
