import { Autor } from './autor.model';
import { Categoria } from './categoria.model';

export interface Libro {
  id: number;
  titulo: string;
  isbn: string;
  autor: Autor;
  categoria: Categoria;
  stock: number;
  anioPublicacion: number | null;
  editorial: string | null;
  portadaUrl: string | null;
}

export interface LibroRequest {
  titulo: string;
  isbn: string;
  autorId: number;
  categoriaId: number;
  stock: number;
  anioPublicacion: number | null;
  editorial: string | null;
  portadaUrl: string | null;
}
