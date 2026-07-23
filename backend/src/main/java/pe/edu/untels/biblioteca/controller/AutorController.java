package pe.edu.untels.biblioteca.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.untels.biblioteca.dto.request.AutorRequest;
import pe.edu.untels.biblioteca.dto.response.AutorResponse;
import pe.edu.untels.biblioteca.service.AutorService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/autores")
@RequiredArgsConstructor
@Tag(name = "Autores", description = "Gestion de autores del catalogo")
public class AutorController {

    private final AutorService autorService;

    @GetMapping
    public ResponseEntity<List<AutorResponse>> listar() {
        return ResponseEntity.ok(autorService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutorResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(autorService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<AutorResponse> crear(@Valid @RequestBody AutorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(autorService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<AutorResponse> actualizar(@PathVariable Long id, @Valid @RequestBody AutorRequest request) {
        return ResponseEntity.ok(autorService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        autorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
