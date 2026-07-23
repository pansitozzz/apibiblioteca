package pe.edu.untels.biblioteca.service;

import org.springframework.data.domain.Pageable;
import pe.edu.untels.biblioteca.dto.response.MultaResponse;
import pe.edu.untels.biblioteca.dto.response.PageResponse;

public interface MultaService {

    PageResponse<MultaResponse> listar(String emailSolicitante, Pageable pageable);

    MultaResponse pagar(Long id);
}
