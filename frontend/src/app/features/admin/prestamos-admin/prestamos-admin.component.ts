import { Component, OnInit, signal } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatButtonModule } from '@angular/material/button';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PrestamoService } from '../../../core/services/prestamo.service';
import { Prestamo } from '../../../core/models/prestamo.model';

@Component({
  selector: 'app-prestamos-admin',
  standalone: true,
  imports: [MatTableModule, MatChipsModule, MatButtonModule, MatPaginatorModule],
  templateUrl: './prestamos-admin.component.html',
  styleUrl: './prestamos-admin.component.scss'
})
export class PrestamosAdminComponent implements OnInit {
  readonly prestamos = signal<Prestamo[]>([]);
  readonly totalElementos = signal(0);
  readonly columnas = ['libro', 'usuario', 'fechaPrestamo', 'fechaDevolucionEsperada', 'estado', 'acciones'];

  pagina = 0;
  tamanio = 10;

  constructor(
    private readonly prestamoService: PrestamoService,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.prestamoService.listar(this.pagina, this.tamanio).subscribe((respuesta) => {
      this.prestamos.set(respuesta.contenido);
      this.totalElementos.set(respuesta.totalElementos);
    });
  }

  cambiarPagina(evento: PageEvent): void {
    this.pagina = evento.pageIndex;
    this.tamanio = evento.pageSize;
    this.cargar();
  }

  devolver(prestamo: Prestamo): void {
    this.prestamoService.devolver(prestamo.id).subscribe({
      next: () => {
        this.snackBar.open('Devolucion registrada correctamente', 'Cerrar', { duration: 3000 });
        this.cargar();
      },
      error: (err) => {
        this.snackBar.open(err?.error?.message ?? 'No se pudo registrar la devolucion', 'Cerrar', { duration: 4000 });
      }
    });
  }
}
