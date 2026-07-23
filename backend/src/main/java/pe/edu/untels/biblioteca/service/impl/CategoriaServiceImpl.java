package pe.edu.untels.biblioteca.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.untels.biblioteca.dto.request.CategoriaRequest;
import pe.edu.untels.biblioteca.dto.response.CategoriaResponse;
import pe.edu.untels.biblioteca.entity.Categoria;
import pe.edu.untels.biblioteca.exception.ResourceNotFoundException;
import pe.edu.untels.biblioteca.mapper.CategoriaMapper;
import pe.edu.untels.biblioteca.repository.CategoriaRepository;
import pe.edu.untels.biblioteca.service.CategoriaService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll().stream()
                .map(categoriaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponse obtenerPorId(Long id) {
        return categoriaMapper.toResponse(buscarOFallar(id));
    }

    @Override
    public CategoriaResponse crear(CategoriaRequest request) {
        Categoria categoria = categoriaMapper.toEntity(request);
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Override
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = buscarOFallar(id);
        categoria.setNombre(request.nombre());
        categoria.setDescripcion(request.descripcion());
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Override
    public void eliminar(Long id) {
        categoriaRepository.delete(buscarOFallar(id));
    }

    private Categoria buscarOFallar(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
    }
}
