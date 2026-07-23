package pe.edu.untels.biblioteca.dto.response;

import pe.edu.untels.biblioteca.entity.EstadoReserva;

import java.time.LocalDate;

public record ReservaResponse(
        Long id,
        LibroResponse libro,
        UsuarioResponse usuario,
        LocalDate fechaReserva,
        EstadoReserva estado
) {
}
