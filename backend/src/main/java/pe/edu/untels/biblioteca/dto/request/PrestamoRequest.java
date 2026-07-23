package pe.edu.untels.biblioteca.dto.request;

import jakarta.validation.constraints.NotNull;

public record PrestamoRequest(
        @NotNull(message = "El libro es obligatorio")
        Long libroId,

        /**
         * Opcional: solo lo usan ADMIN/BIBLIOTECARIO para registrar un prestamo
         * a nombre de otro usuario. Si es null, el prestamo se crea para el
         * usuario autenticado (caso tipico de un ESTUDIANTE).
         */
        Long usuarioId
) {
}
