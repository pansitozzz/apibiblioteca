import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AutorService } from '../../../core/services/autor.service';
import { Autor } from '../../../core/models/autor.model';

@Component({
  selector: 'app-autores-admin',
  standalone: true,
  imports: [ReactiveFormsModule, MatTableModule, MatButtonModule, MatIconModule, MatFormFieldModule, MatInputModule],
  templateUrl: './autores-admin.component.html',
  styleUrl: './autores-admin.component.scss'
})
export class AutoresAdminComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly autorService = inject(AutorService);
  private readonly snackBar = inject(MatSnackBar);

  readonly autores = signal<Autor[]>([]);
  readonly editandoId = signal<number | null>(null);
  readonly mostrarFormulario = signal(false);
  readonly columnas = ['nombre', 'apellido', 'nacionalidad', 'acciones'];

  readonly formulario = this.fb.group({
    nombre: ['', Validators.required],
    apellido: ['', Validators.required],
    nacionalidad: [''],
    biografia: ['']
  });

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.autorService.listar().subscribe((autores) => this.autores.set(autores));
  }

  nuevo(): void {
    this.editandoId.set(null);
    this.formulario.reset();
    this.mostrarFormulario.set(true);
  }

  editar(autor: Autor): void {
    this.editandoId.set(autor.id);
    this.formulario.setValue({
      nombre: autor.nombre,
      apellido: autor.apellido,
      nacionalidad: autor.nacionalidad ?? '',
      biografia: autor.biografia ?? ''
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
      nombre: valores.nombre!,
      apellido: valores.apellido!,
      nacionalidad: valores.nacionalidad || null,
      biografia: valores.biografia || null
    };

    const id = this.editandoId();
    const operacion = id ? this.autorService.actualizar(id, request) : this.autorService.crear(request);

    operacion.subscribe({
      next: () => {
        this.snackBar.open('Autor guardado correctamente', 'Cerrar', { duration: 3000 });
        this.mostrarFormulario.set(false);
        this.cargar();
      },
      error: (err) => this.snackBar.open(err?.error?.message ?? 'No se pudo guardar el autor', 'Cerrar', { duration: 4000 })
    });
  }

  eliminar(autor: Autor): void {
    if (!confirm(`Eliminar al autor "${autor.nombre} ${autor.apellido}"?`)) return;

    this.autorService.eliminar(autor.id).subscribe({
      next: () => {
        this.snackBar.open('Autor eliminado', 'Cerrar', { duration: 3000 });
        this.cargar();
      },
      error: (err) => this.snackBar.open(err?.error?.message ?? 'No se pudo eliminar el autor', 'Cerrar', { duration: 4000 })
    });
  }
}
