package pe.edu.untels.biblioteca.mapper;

import org.mapstruct.Mapper;
import pe.edu.untels.biblioteca.dto.response.ReservaResponse;
import pe.edu.untels.biblioteca.entity.Reserva;

@Mapper(componentModel = "spring", uses = {LibroMapper.class, UsuarioMapper.class})
public interface ReservaMapper {

    ReservaResponse toResponse(Reserva reserva);
}
