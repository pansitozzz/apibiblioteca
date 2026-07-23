export interface Autor {
  id: number;
  nombre: string;
  apellido: string;
  nacionalidad: string | null;
  biografia: string | null;
}

export interface AutorRequest {
  nombre: string;
  apellido: string;
  nacionalidad: string | null;
  biografia: string | null;
}
