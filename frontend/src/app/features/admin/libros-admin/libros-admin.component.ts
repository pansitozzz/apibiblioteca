import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { LibroService } from '../../../core/services/libro.service';
import { AutorService } from '../../../core/services/autor.service';
import { CategoriaService } from '../../../core/services/categoria.service';
import { Libro } from '../../../core/models/libro.model';
import { Autor } from '../../../core/models/autor.model';
import { Categoria } from '../../../core/models/categoria.model';

@Component({
  selector: 'app-libros-admin',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule
  ],
  templateUrl: './libros-admin.component.html',
  styleUrl: './libros-admin.component.scss'
})
export class LibrosAdminComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly libroService = inject(LibroService);
  private readonly autorService = inject(AutorService);
  private readonly categoriaService = inject(CategoriaService);
  private readonly snackBar = inject(MatSnackBar);

  readonly libros = signal<Libro[]>([]);
  readonly autores = signal<Autor[]>([]);
  readonly categorias = signal<Categoria[]>([]);
  readonly editandoId = signal<number | null>(null);
  readonly mostrarFormulario = signal(false);
  readonly columnas = ['titulo', 'autor', 'categoria', 'stock', 'acciones'];

  readonly formulario = this.fb.group({
    titulo: ['', Validators.required],
    isbn: ['', Validators.required],
    autorId: [null as number | null, Validators.required],
    categoriaId: [null as number | null, Validators.required],
    stock: [0, [Validators.required, Validators.min(0)]],
    anioPublicacion: [null as number | null],
    editorial: [''],
    portadaUrl: ['']
  });

  ngOnInit(): void {
    this.cargarLibros();
    this.autorService.listar().subscribe((autores) => this.autores.set(autores));
    this.categoriaService.listar().subscribe((categorias) => this.categorias.set(categorias));
  }

  cargarLibros(): void {
    this.libroService.buscar({ page: 0, size: 50 }).subscribe((respuesta) => this.libros.set(respuesta.contenido));
  }

  nuevoLibro(): void {
    this.editandoId.set(null);
    this.formulario.reset({ stock: 0 });
    this.mostrarFormulario.set(true);
  }

  editar(libro: Libro): void {
    this.editandoId.set(libro.id);
    this.formulario.setValue({
      titulo: libro.titulo,
      isbn: libro.isbn,
      autorId: libro.autor.id,
      categoriaId: libro.categoria.id,
      stock: libro.stock,
      anioPublicacion: libro.anioPublicacion,
      editorial: libro.editorial ?? '',
      portadaUrl: libro.portadaUrl ?? ''
    });
    this.mostrarFormulario.set(true);
  }

  cancelar(): void {
    this.mostrarFormulario.set(false);
  }

  guardar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    const valores = this.formulario.getRawValue();
    const request = {
      titulo: valores.titulo!,
      isbn: valores.isbn!,
      autorId: valores.autorId!,
      categoriaId: valores.categoriaId!,
      stock: valores.stock!,
      anioPublicacion: valores.anioPublicacion,
      editorial: valores.editorial || null,
      portadaUrl: valores.portadaUrl || null
    };

    const id = this.editandoId();
    const operacion = id ? this.libroService.actualizar(id, request) : this.libroService.crear(request);

    operacion.subscribe({
      next: () => {
        this.snackBar.open('Libro guardado correctamente', 'Cerrar', { duration: 3000 });
        this.mostrarFormulario.set(false);
        this.cargarLibros();
      },
      error: (err) => {
        this.snackBar.open(err?.error?.message ?? 'No se pudo guardar el libro', 'Cerrar', { duration: 4000 });
      }
    });
  }

  eliminar(libro: Libro): void {
    if (!confirm(`Eliminar el libro "${libro.titulo}"?`)) return;

    this.libroService.eliminar(libro.id).subscribe({
      next: () => {
        this.snackBar.open('Libro eliminado', 'Cerrar', { duration: 3000 });
        this.cargarLibros();
      },
      error: (err) => {
        this.snackBar.open(err?.error?.message ?? 'No se pudo eliminar el libro', 'Cerrar', { duration: 4000 });
      }
    });
  }
}
