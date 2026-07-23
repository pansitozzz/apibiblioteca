package pe.edu.untels.biblioteca.service;

import org.springframework.data.domain.Pageable;
import pe.edu.untels.biblioteca.dto.request.PrestamoRequest;
import pe.edu.untels.biblioteca.dto.response.PageResponse;
import pe.edu.untels.biblioteca.dto.response.PrestamoResponse;

public interface PrestamoService {

    PrestamoResponse crear(PrestamoRequest request, String emailSolicitante);

    PageResponse<PrestamoResponse> listar(String emailSolicitante, Pageable pageable);

    PrestamoResponse devolver(Long id);
}
