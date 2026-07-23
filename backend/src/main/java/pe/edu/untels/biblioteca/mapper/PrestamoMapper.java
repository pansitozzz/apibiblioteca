package pe.edu.untels.biblioteca.mapper;

import org.mapstruct.Mapper;
import pe.edu.untels.biblioteca.dto.response.PrestamoResponse;
import pe.edu.untels.biblioteca.entity.Prestamo;

@Mapper(componentModel = "spring", uses = {LibroMapper.class, UsuarioMapper.class})
public interface PrestamoMapper {

    PrestamoResponse toResponse(Prestamo prestamo);
}
