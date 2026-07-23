import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatChipsModule } from '@angular/material/chips';
import { MatSnackBar } from '@angular/material/snack-bar';
import { LibroService } from '../../../core/services/libro.service';
import { PrestamoService } from '../../../core/services/prestamo.service';
import { ReservaService } from '../../../core/services/reserva.service';
import { Libro } from '../../../core/models/libro.model';

@Component({
  selector: 'app-catalogo-list',
  standalone: true,
  imports: [
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatPaginatorModule,
    MatChipsModule
  ],
  templateUrl: './catalogo-list.component.html',
  styleUrl: './catalogo-list.component.scss'
})
export class CatalogoListComponent implements OnInit {
  readonly libros = signal<Libro[]>([]);
  readonly totalElementos = signal(0);
  readonly cargando = signal(false);

  titulo = '';
  autor = '';
  pagina = 0;
  tamanio = 8;

  constructor(
    private readonly libroService: LibroService,
    private readonly prestamoService: PrestamoService,
    private readonly reservaService: ReservaService,
    private readonly router: Router,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.buscar();
  }

  buscar(): void {
    this.cargando.set(true);
    this.libroService
      .buscar({ titulo: this.titulo, autor: this.autor, page: this.pagina, size: this.tamanio })
      .subscribe({
        next: (respuesta) => {
          this.libros.set(respuesta.contenido);
          this.totalElementos.set(respuesta.totalElementos);
          this.cargando.set(false);
        },
        error: () => this.cargando.set(false)
      });
  }

  cambiarPagina(evento: PageEvent): void {
    this.pagina = evento.pageIndex;
    this.tamanio = evento.pageSize;
    this.buscar();
  }

  verDetalle(libro: Libro): void {
    this.router.navigate(['/catalogo', libro.id]);
  }

  solicitarPrestamo(libro: Libro, evento: Event): void {
    evento.stopPropagation();
    this.prestamoService.crear({ libroId: libro.id, usuarioId: null }).subscribe({
      next: () => {
        this.snackBar.open('Prestamo registrado correctamente', 'Cerrar', { duration: 3000 });
        this.buscar();
      },
      error: (err) => {
        this.snackBar.open(err?.error?.message ?? 'No se pudo registrar el prestamo', 'Cerrar', { duration: 4000 });
      }
    });
  }

  reservar(libro: Libro, evento: Event): void {
    evento.stopPropagation();
    this.reservaService.crear({ libroId: libro.id }).subscribe({
      next: () => this.snackBar.open('Reserva registrada correctamente', 'Cerrar', { duration: 3000 }),
      error: (err) => {
        this.snackBar.open(err?.error?.message ?? 'No se pudo registrar la reserva', 'Cerrar', { duration: 4000 });
      }
    });
  }
}
