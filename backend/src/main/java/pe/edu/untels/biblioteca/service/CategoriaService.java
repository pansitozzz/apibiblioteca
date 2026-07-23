package pe.edu.untels.biblioteca.service;

import pe.edu.untels.biblioteca.dto.request.CategoriaRequest;
import pe.edu.untels.biblioteca.dto.response.CategoriaResponse;

import java.util.List;

public interface CategoriaService {

    List<CategoriaResponse> listar();

    CategoriaResponse obtenerPorId(Long id);

    CategoriaResponse crear(CategoriaRequest request);

    CategoriaResponse actualizar(Long id, CategoriaRequest request);

    void eliminar(Long id);
}
