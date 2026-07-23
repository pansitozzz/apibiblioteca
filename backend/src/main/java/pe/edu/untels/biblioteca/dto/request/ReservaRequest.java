package pe.edu.untels.biblioteca.dto.request;

import jakarta.validation.constraints.NotNull;

public record ReservaRequest(
        @NotNull(message = "El libro es obligatorio")
        Long libroId
) {
}
