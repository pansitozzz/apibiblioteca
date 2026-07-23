package pe.edu.untels.biblioteca.service;

import org.springframework.data.domain.Pageable;
import pe.edu.untels.biblioteca.dto.request.LibroRequest;
import pe.edu.untels.biblioteca.dto.response.LibroResponse;
import pe.edu.untels.biblioteca.dto.response.PageResponse;

public interface LibroService {

    PageResponse<LibroResponse> buscar(String titulo, String autor, Long categoriaId, Pageable pageable);

    LibroResponse obtenerPorId(Long id);

    LibroResponse crear(LibroRequest request);

    LibroResponse actualizar(Long id, LibroRequest request);

    void eliminar(Long id);
}
