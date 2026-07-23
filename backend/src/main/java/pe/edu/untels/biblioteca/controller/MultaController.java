package pe.edu.untels.biblioteca.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pe.edu.untels.biblioteca.dto.response.MultaResponse;
import pe.edu.untels.biblioteca.dto.response.PageResponse;
import pe.edu.untels.biblioteca.entity.Usuario;
import pe.edu.untels.biblioteca.service.MultaService;

@RestController
@RequestMapping("/api/v1/multas")
@RequiredArgsConstructor
@Tag(name = "Multas", description = "Multas generadas por devoluciones atrasadas")
public class MultaController {

    private final MultaService multaService;

    @GetMapping
    public ResponseEntity<PageResponse<MultaResponse>> listar(
            @AuthenticationPrincipal Usuario usuario,
            @PageableDefault(size = 10, sort = "fechaGeneracion") Pageable pageable) {
        return ResponseEntity.ok(multaService.listar(usuario.getEmail(), pageable));
    }

    @PutMapping("/{id}/pagar")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<MultaResponse> pagar(@PathVariable Long id) {
        return ResponseEntity.ok(multaService.pagar(id));
    }
}
