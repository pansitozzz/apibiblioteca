package pe.edu.untels.biblioteca.dto.response;

import pe.edu.untels.biblioteca.entity.EstadoPrestamo;

import java.time.LocalDate;

public record PrestamoResponse(
        Long id,
        LibroResponse libro,
        UsuarioResponse usuario,
        LocalDate fechaPrestamo,
        LocalDate fechaDevolucionEsperada,
        LocalDate fechaDevolucionReal,
        EstadoPrestamo estado
) {
}
