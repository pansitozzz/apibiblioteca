package pe.edu.untels.biblioteca.mapper;

import org.mapstruct.Mapper;
import pe.edu.untels.biblioteca.dto.request.CategoriaRequest;
import pe.edu.untels.biblioteca.dto.response.CategoriaResponse;
import pe.edu.untels.biblioteca.entity.Categoria;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    CategoriaResponse toResponse(Categoria categoria);

    Categoria toEntity(CategoriaRequest request);
}
