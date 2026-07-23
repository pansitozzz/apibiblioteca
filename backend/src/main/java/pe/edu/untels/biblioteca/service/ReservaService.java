package pe.edu.untels.biblioteca.service;

import org.springframework.data.domain.Pageable;
import pe.edu.untels.biblioteca.dto.request.ReservaRequest;
import pe.edu.untels.biblioteca.dto.response.PageResponse;
import pe.edu.untels.biblioteca.dto.response.ReservaResponse;

public interface ReservaService {

    ReservaResponse crear(ReservaRequest request, String emailSolicitante);

    PageResponse<ReservaResponse> listar(String emailSolicitante, Pageable pageable);

    ReservaResponse cancelar(Long id, String emailSolicitante);
}
