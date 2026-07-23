package pe.edu.untels.biblioteca.service;

import pe.edu.untels.biblioteca.dto.request.UsuarioUpdateRequest;
import pe.edu.untels.biblioteca.dto.response.UsuarioResponse;

import java.util.List;

public interface UsuarioService {

    List<UsuarioResponse> listar();

    UsuarioResponse obtenerPorId(Long id);

    UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request);
}
