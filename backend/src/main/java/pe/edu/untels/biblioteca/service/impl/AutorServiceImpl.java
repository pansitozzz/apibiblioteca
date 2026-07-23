package pe.edu.untels.biblioteca.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.untels.biblioteca.dto.request.AutorRequest;
import pe.edu.untels.biblioteca.dto.response.AutorResponse;
import pe.edu.untels.biblioteca.entity.Autor;
import pe.edu.untels.biblioteca.exception.ResourceNotFoundException;
import pe.edu.untels.biblioteca.mapper.AutorMapper;
import pe.edu.untels.biblioteca.repository.AutorRepository;
import pe.edu.untels.biblioteca.service.AutorService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AutorServiceImpl implements AutorService {

    private final AutorRepository autorRepository;
    private final AutorMapper autorMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AutorResponse> listar() {
        return autorRepository.findAll().stream()
                .map(autorMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AutorResponse obtenerPorId(Long id) {
        return autorMapper.toResponse(buscarOFallar(id));
    }

    @Override
    public AutorResponse crear(AutorRequest request) {
        Autor autor = autorMapper.toEntity(request);
        return autorMapper.toResponse(autorRepository.save(autor));
    }

    @Override
    public AutorResponse actualizar(Long id, AutorRequest request) {
        Autor autor = buscarOFallar(id);
        autor.setNombre(request.nombre());
        autor.setApellido(request.apellido());
        autor.setNacionalidad(request.nacionalidad());
        autor.setBiografia(request.biografia());
        return autorMapper.toResponse(autorRepository.save(autor));
    }

    @Override
    public void eliminar(Long id) {
        Autor autor = buscarOFallar(id);
        autorRepository.delete(autor);
    }

    private Autor buscarOFallar(Long id) {
        return autorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autor no encontrado con id: " + id));
    }
}
