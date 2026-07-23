package pe.edu.untels.biblioteca.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UsuarioUpdateRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        String apellido,

        String codigoEstudiante
) {
}
