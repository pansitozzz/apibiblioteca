package pe.edu.untels.biblioteca.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pe.edu.untels.biblioteca.dto.request.PrestamoRequest;
import pe.edu.untels.biblioteca.dto.response.PageResponse;
import pe.edu.untels.biblioteca.dto.response.PrestamoResponse;
import pe.edu.untels.biblioteca.entity.Usuario;
import pe.edu.untels.biblioteca.service.PrestamoService;

@RestController
@RequestMapping("/api/v1/prestamos")
@RequiredArgsConstructor
@Tag(name = "Prestamos", description = "Prestamos y devoluciones de libros")
public class PrestamoController {

    private final PrestamoService prestamoService;

    @PostMapping
    public ResponseEntity<PrestamoResponse> crear(@Valid @RequestBody PrestamoRequest request,
                                                    @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prestamoService.crear(request, usuario.getEmail()));
    }

    @GetMapping
    public ResponseEntity<PageResponse<PrestamoResponse>> listar(
            @AuthenticationPrincipal Usuario usuario,
            @PageableDefault(size = 10, sort = "fechaPrestamo") Pageable pageable) {
        return ResponseEntity.ok(prestamoService.listar(usuario.getEmail(), pageable));
    }

    @PutMapping("/{id}/devolver")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<PrestamoResponse> devolver(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.devolver(id));
    }
}
