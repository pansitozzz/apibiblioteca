import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-acceso-denegado',
  standalone: true,
  imports: [RouterLink, MatButtonModule, MatIconModule],
  template: `
    <div class="contenedor">
      <mat-icon class="icono">block</mat-icon>
      <h1>Acceso denegado</h1>
      <p>No tienes permisos para ver esta seccion.</p>
      <button mat-raised-button color="primary" routerLink="/catalogo">Volver al catalogo</button>
    </div>
  `,
  styles: [`
    .contenedor {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 64px 24px;
      text-align: center;
      gap: 8px;
    }
    .icono {
      font-size: 64px;
      width: 64px;
      height: 64px;
      opacity: 0.6;
    }
  `]
})
export class AccesoDenegadoComponent {}
