package pe.edu.untels.biblioteca.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.untels.biblioteca.dto.request.UsuarioUpdateRequest;
import pe.edu.untels.biblioteca.dto.response.UsuarioResponse;
import pe.edu.untels.biblioteca.entity.Usuario;
import pe.edu.untels.biblioteca.exception.ResourceNotFoundException;
import pe.edu.untels.biblioteca.mapper.UsuarioMapper;
import pe.edu.untels.biblioteca.repository.UsuarioRepository;
import pe.edu.untels.biblioteca.service.UsuarioService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorId(Long id) {
        return usuarioMapper.toResponse(buscarOFallar(id));
    }

    @Override
    public UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = buscarOFallar(id);
        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setCodigoEstudiante(request.codigoEstudiante());
        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    private Usuario buscarOFallar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }
}
