package pe.edu.untels.biblioteca.dto.request;

import jakarta.validation.constraints.*;

public record LibroRequest(
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 200, message = "El título no puede superar 200 caracteres")
        String titulo,

        @NotBlank(message = "El ISBN es obligatorio")
        @Size(max = 20, message = "El ISBN no puede superar 20 caracteres")
        String isbn,

        @NotNull(message = "El autor es obligatorio")
        Long autorId,

        @NotNull(message = "La categoría es obligatoria")
        Long categoriaId,

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer stock,

        @Min(value = 1450, message = "El año de publicación no es válido")
        Integer anioPublicacion,

        @Size(max = 120, message = "La editorial no puede superar 120 caracteres")
        String editorial,

        String portadaUrl
) {
}
