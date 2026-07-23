package pe.edu.untels.biblioteca.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pe.edu.untels.biblioteca.dto.request.ReservaRequest;
import pe.edu.untels.biblioteca.dto.response.PageResponse;
import pe.edu.untels.biblioteca.dto.response.ReservaResponse;
import pe.edu.untels.biblioteca.entity.Usuario;
import pe.edu.untels.biblioteca.service.ReservaService;

@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Reservas de libros sin stock disponible")
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    public ResponseEntity<ReservaResponse> crear(@Valid @RequestBody ReservaRequest request,
                                                   @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.crear(request, usuario.getEmail()));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ReservaResponse>> listar(
            @AuthenticationPrincipal Usuario usuario,
            @PageableDefault(size = 10, sort = "fechaReserva") Pageable pageable) {
        return ResponseEntity.ok(reservaService.listar(usuario.getEmail(), pageable));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponse> cancelar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(reservaService.cancelar(id, usuario.getEmail()));
    }
}
