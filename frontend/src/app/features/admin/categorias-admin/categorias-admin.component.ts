import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CategoriaService } from '../../../core/services/categoria.service';
import { Categoria } from '../../../core/models/categoria.model';

@Component({
  selector: 'app-categorias-admin',
  standalone: true,
  imports: [ReactiveFormsModule, MatTableModule, MatButtonModule, MatIconModule, MatFormFieldModule, MatInputModule],
  templateUrl: './categorias-admin.component.html',
  styleUrl: './categorias-admin.component.scss'
})
export class CategoriasAdminComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly categoriaService = inject(CategoriaService);
  private readonly snackBar = inject(MatSnackBar);

  readonly categorias = signal<Categoria[]>([]);
  readonly editandoId = signal<number | null>(null);
  readonly mostrarFormulario = signal(false);
  readonly columnas = ['nombre', 'descripcion', 'acciones'];

  readonly formulario = this.fb.group({
    nombre: ['', Validators.required],
    descripcion: ['']
  });

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.categoriaService.listar().subscribe((categorias) => this.categorias.set(categorias));
  }

  nueva(): void {
    this.editandoId.set(null);
    this.formulario.reset();
    this.mostrarFormulario.set(true);
  }

  editar(categoria: Categoria): void {
    this.editandoId.set(categoria.id);
    this.formulario.setValue({
      nombre: categoria.nombre,
      descripcion: categoria.descripcion ?? ''
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
    const request = { nombre: valores.nombre!, descripcion: valores.descripcion || null };

    const id = this.editandoId();
    const operacion = id ? this.categoriaService.actualizar(id, request) : this.categoriaService.crear(request);

    operacion.subscribe({
      next: () => {
        this.snackBar.open('Categoría guardada correctamente', 'Cerrar', { duration: 3000 });
        this.mostrarFormulario.set(false);
        this.cargar();
      },
      error: (err) => this.snackBar.open(err?.error?.message ?? 'No se pudo guardar la categoría', 'Cerrar', { duration: 4000 })
    });
  }

  eliminar(categoria: Categoria): void {
    if (!confirm(`Eliminar la categoría "${categoria.nombre}"?`)) return;

    this.categoriaService.eliminar(categoria.id).subscribe({
      next: () => {
        this.snackBar.open('Categoría eliminada', 'Cerrar', { duration: 3000 });
        this.cargar();
      },
      error: (err) => this.snackBar.open(err?.error?.message ?? 'No se pudo eliminar la categoría', 'Cerrar', { duration: 4000 })
    });
  }
}
