package pe.edu.untels.biblioteca.mapper;

import org.mapstruct.Mapper;
import pe.edu.untels.biblioteca.dto.response.MultaResponse;
import pe.edu.untels.biblioteca.entity.Multa;

@Mapper(componentModel = "spring", uses = {PrestamoMapper.class})
public interface MultaMapper {

    MultaResponse toResponse(Multa multa);
}
