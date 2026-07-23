package pe.edu.untels.biblioteca.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.untels.biblioteca.dto.request.ReservaRequest;
import pe.edu.untels.biblioteca.dto.response.PageResponse;
import pe.edu.untels.biblioteca.dto.response.ReservaResponse;
import pe.edu.untels.biblioteca.entity.*;
import pe.edu.untels.biblioteca.exception.BusinessRuleException;
import pe.edu.untels.biblioteca.exception.ResourceNotFoundException;
import pe.edu.untels.biblioteca.mapper.ReservaMapper;
import pe.edu.untels.biblioteca.repository.LibroRepository;
import pe.edu.untels.biblioteca.repository.ReservaRepository;
import pe.edu.untels.biblioteca.repository.UsuarioRepository;
import pe.edu.untels.biblioteca.service.ReservaService;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final LibroRepository libroRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaMapper reservaMapper;

    @Override
    public ReservaResponse crear(ReservaRequest request, String emailSolicitante) {
        Usuario solicitante = obtenerUsuario(emailSolicitante);

        Libro libro = libroRepository.findById(request.libroId())
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + request.libroId()));

        if (libro.getStock() > 0) {
            throw new BusinessRuleException(
                    "El libro tiene stock disponible, no es necesario reservarlo: puede solicitar un prestamo directamente");
        }

        Reserva reserva = Reserva.builder()
                .libro(libro)
                .usuario(solicitante)
                .fechaReserva(LocalDate.now())
                .estado(EstadoReserva.PENDIENTE)
                .build();

        return reservaMapper.toResponse(reservaRepository.save(reserva));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReservaResponse> listar(String emailSolicitante, Pageable pageable) {
        Usuario solicitante = obtenerUsuario(emailSolicitante);

        Page<Reserva> pagina = (solicitante.getRol() == Rol.ESTUDIANTE)
                ? reservaRepository.findByUsuarioId(solicitante.getId(), pageable)
                : reservaRepository.findAll(pageable);

        return PageResponse.from(pagina.map(reservaMapper::toResponse));
    }

    @Override
    public ReservaResponse cancelar(Long id, String emailSolicitante) {
        Usuario solicitante = obtenerUsuario(emailSolicitante);
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));

        boolean esDueno = reserva.getUsuario().getId().equals(solicitante.getId());
        boolean esPrivilegiado = solicitante.getRol() == Rol.ADMIN || solicitante.getRol() == Rol.BIBLIOTECARIO;
        if (!esDueno && !esPrivilegiado) {
            throw new AccessDeniedException("No puede cancelar reservas de otro usuario");
        }

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new BusinessRuleException("Solo se pueden cancelar reservas pendientes");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        return reservaMapper.toResponse(reservaRepository.save(reserva));
    }

    private Usuario obtenerUsuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
    }
}
