package pe.edu.untels.biblioteca.dto.response;

import pe.edu.untels.biblioteca.entity.Rol;

public record UsuarioResponse(
        Long id,
        String nombre,
        String apellido,
        String email,
        Rol rol,
        String codigoEstudiante
) {
}
