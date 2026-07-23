import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatSnackBar } from '@angular/material/snack-bar';
import { LibroService } from '../../../core/services/libro.service';
import { PrestamoService } from '../../../core/services/prestamo.service';
import { ReservaService } from '../../../core/services/reserva.service';
import { Libro } from '../../../core/models/libro.model';

@Component({
  selector: 'app-libro-detalle',
  standalone: true,
  imports: [RouterLink, MatCardModule, MatButtonModule, MatChipsModule],
  templateUrl: './libro-detalle.component.html',
  styleUrl: './libro-detalle.component.scss'
})
export class LibroDetalleComponent implements OnInit {
  readonly libro = signal<Libro | null>(null);

  constructor(
    private readonly route: ActivatedRoute,
    private readonly libroService: LibroService,
    private readonly prestamoService: PrestamoService,
    private readonly reservaService: ReservaService,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.libroService.obtenerPorId(id).subscribe((libro) => this.libro.set(libro));
  }

  solicitarPrestamo(): void {
    const libro = this.libro();
    if (!libro) return;

    this.prestamoService.crear({ libroId: libro.id, usuarioId: null }).subscribe({
      next: () => {
        this.snackBar.open('Prestamo registrado correctamente', 'Cerrar', { duration: 3000 });
        this.libroService.obtenerPorId(libro.id).subscribe((actualizado) => this.libro.set(actualizado));
      },
      error: (err) => {
        this.snackBar.open(err?.error?.message ?? 'No se pudo registrar el prestamo', 'Cerrar', { duration: 4000 });
      }
    });
  }

  reservar(): void {
    const libro = this.libro();
    if (!libro) return;

    this.reservaService.crear({ libroId: libro.id }).subscribe({
      next: () => this.snackBar.open('Reserva registrada correctamente', 'Cerrar', { duration: 3000 }),
      error: (err) => {
        this.snackBar.open(err?.error?.message ?? 'No se pudo registrar la reserva', 'Cerrar', { duration: 4000 });
      }
    });
  }
}
