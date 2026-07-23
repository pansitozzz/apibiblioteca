import { Component, OnInit, signal } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { PrestamoService } from '../../../core/services/prestamo.service';
import { Prestamo } from '../../../core/models/prestamo.model';

@Component({
  selector: 'app-mis-prestamos',
  standalone: true,
  imports: [MatTableModule, MatChipsModule, MatPaginatorModule],
  templateUrl: './mis-prestamos.component.html',
  styleUrl: './mis-prestamos.component.scss'
})
export class MisPrestamosComponent implements OnInit {
  readonly prestamos = signal<Prestamo[]>([]);
  readonly totalElementos = signal(0);
  readonly columnas = ['titulo', 'fechaPrestamo', 'fechaDevolucionEsperada', 'estado'];

  pagina = 0;
  tamanio = 10;

  constructor(private readonly prestamoService: PrestamoService) {}

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
}
