package pe.edu.untels.biblioteca.dto.response;

public record AutorResponse(
        Long id,
        String nombre,
        String apellido,
        String nacionalidad,
        String biografia
) {
}
