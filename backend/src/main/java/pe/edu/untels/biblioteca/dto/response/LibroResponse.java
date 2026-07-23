package pe.edu.untels.biblioteca.dto.response;

public record LibroResponse(
        Long id,
        String titulo,
        String isbn,
        AutorResponse autor,
        CategoriaResponse categoria,
        Integer stock,
        Integer anioPublicacion,
        String editorial,
        String portadaUrl
) {
}
