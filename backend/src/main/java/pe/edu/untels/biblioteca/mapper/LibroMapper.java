package pe.edu.untels.biblioteca.mapper;

import org.mapstruct.Mapper;
import pe.edu.untels.biblioteca.dto.response.LibroResponse;
import pe.edu.untels.biblioteca.entity.Libro;

@Mapper(componentModel = "spring", uses = {AutorMapper.class, CategoriaMapper.class})
public interface LibroMapper {

    LibroResponse toResponse(Libro libro);
}
