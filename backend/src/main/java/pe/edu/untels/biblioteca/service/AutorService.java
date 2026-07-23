package pe.edu.untels.biblioteca.service;

import pe.edu.untels.biblioteca.dto.request.AutorRequest;
import pe.edu.untels.biblioteca.dto.response.AutorResponse;

import java.util.List;

public interface AutorService {

    List<AutorResponse> listar();

    AutorResponse obtenerPorId(Long id);

    AutorResponse crear(AutorRequest request);

    AutorResponse actualizar(Long id, AutorRequest request);

    void eliminar(Long id);
}
