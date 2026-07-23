package pe.edu.untels.biblioteca.mapper;

import org.mapstruct.Mapper;
import pe.edu.untels.biblioteca.dto.request.AutorRequest;
import pe.edu.untels.biblioteca.dto.response.AutorResponse;
import pe.edu.untels.biblioteca.entity.Autor;

@Mapper(componentModel = "spring")
public interface AutorMapper {

    AutorResponse toResponse(Autor autor);

    Autor toEntity(AutorRequest request);
}
