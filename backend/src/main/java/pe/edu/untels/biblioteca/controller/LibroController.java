package pe.edu.untels.biblioteca.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.untels.biblioteca.dto.request.LibroRequest;
import pe.edu.untels.biblioteca.dto.response.LibroResponse;
import pe.edu.untels.biblioteca.dto.response.PageResponse;
import pe.edu.untels.biblioteca.service.LibroService;

@RestController
@RequestMapping("/api/v1/libros")
@RequiredArgsConstructor
@Tag(name = "Libros", description = "Catálogo de libros de la biblioteca")
public class LibroController {

    private final LibroService libroService;

    @GetMapping
    public ResponseEntity<PageResponse<LibroResponse>> buscar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) Long categoriaId,
            @PageableDefault(size = 10, sort = "titulo") Pageable pageable) {
        return ResponseEntity.ok(libroService.buscar(titulo, autor, categoriaId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibroResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(libroService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<LibroResponse> crear(@Valid @RequestBody LibroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(libroService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<LibroResponse> actualizar(@PathVariable Long id, @Valid @RequestBody LibroRequest request) {
        return ResponseEntity.ok(libroService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        libroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
