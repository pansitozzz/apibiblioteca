package pe.edu.untels.biblioteca.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.untels.biblioteca.dto.request.LibroRequest;
import pe.edu.untels.biblioteca.dto.response.LibroResponse;
import pe.edu.untels.biblioteca.dto.response.PageResponse;
import pe.edu.untels.biblioteca.entity.Autor;
import pe.edu.untels.biblioteca.entity.Categoria;
import pe.edu.untels.biblioteca.entity.Libro;
import pe.edu.untels.biblioteca.exception.BusinessRuleException;
import pe.edu.untels.biblioteca.exception.ResourceNotFoundException;
import pe.edu.untels.biblioteca.mapper.LibroMapper;
import pe.edu.untels.biblioteca.repository.AutorRepository;
import pe.edu.untels.biblioteca.repository.CategoriaRepository;
import pe.edu.untels.biblioteca.repository.LibroRepository;
import pe.edu.untels.biblioteca.service.LibroService;

@Service
@RequiredArgsConstructor
@Transactional
public class LibroServiceImpl implements LibroService {

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private final CategoriaRepository categoriaRepository;
    private final LibroMapper libroMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<LibroResponse> buscar(String titulo, String autor, Long categoriaId, Pageable pageable) {
        Page<Libro> pagina = libroRepository.buscar(
                blankToNull(titulo), blankToNull(autor), categoriaId, pageable);
        return PageResponse.from(pagina.map(libroMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public LibroResponse obtenerPorId(Long id) {
        return libroMapper.toResponse(buscarOFallar(id));
    }

    @Override
    public LibroResponse crear(LibroRequest request) {
        if (libroRepository.existsByIsbn(request.isbn())) {
            throw new BusinessRuleException("Ya existe un libro registrado con el ISBN: " + request.isbn());
        }

        Autor autor = autorRepository.findById(request.autorId())
                .orElseThrow(() -> new ResourceNotFoundException("Autor no encontrado con id: " + request.autorId()));
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + request.categoriaId()));

        Libro libro = Libro.builder()
                .titulo(request.titulo())
                .isbn(request.isbn())
                .autor(autor)
                .categoria(categoria)
                .stock(request.stock())
                .anioPublicacion(request.anioPublicacion())
                .editorial(request.editorial())
                .portadaUrl(request.portadaUrl())
                .build();

        return libroMapper.toResponse(libroRepository.save(libro));
    }

    @Override
    public LibroResponse actualizar(Long id, LibroRequest request) {
        Libro libro = buscarOFallar(id);

        if (!libro.getIsbn().equals(request.isbn()) && libroRepository.existsByIsbn(request.isbn())) {
            throw new BusinessRuleException("Ya existe un libro registrado con el ISBN: " + request.isbn());
        }

        Autor autor = autorRepository.findById(request.autorId())
                .orElseThrow(() -> new ResourceNotFoundException("Autor no encontrado con id: " + request.autorId()));
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + request.categoriaId()));

        libro.setTitulo(request.titulo());
        libro.setIsbn(request.isbn());
        libro.setAutor(autor);
        libro.setCategoria(categoria);
        libro.setStock(request.stock());
        libro.setAnioPublicacion(request.anioPublicacion());
        libro.setEditorial(request.editorial());
        libro.setPortadaUrl(request.portadaUrl());

        return libroMapper.toResponse(libroRepository.save(libro));
    }

    @Override
    public void eliminar(Long id) {
        libroRepository.delete(buscarOFallar(id));
    }

    private Libro buscarOFallar(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + id));
    }

    private String blankToNull(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor;
    }
}
