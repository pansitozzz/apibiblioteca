package pe.edu.untels.biblioteca.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.untels.biblioteca.dto.request.PrestamoRequest;
import pe.edu.untels.biblioteca.dto.response.PageResponse;
import pe.edu.untels.biblioteca.dto.response.PrestamoResponse;
import pe.edu.untels.biblioteca.entity.*;
import pe.edu.untels.biblioteca.exception.BusinessRuleException;
import pe.edu.untels.biblioteca.exception.ResourceNotFoundException;
import pe.edu.untels.biblioteca.mapper.PrestamoMapper;
import pe.edu.untels.biblioteca.repository.LibroRepository;
import pe.edu.untels.biblioteca.repository.MultaRepository;
import pe.edu.untels.biblioteca.repository.PrestamoRepository;
import pe.edu.untels.biblioteca.repository.UsuarioRepository;
import pe.edu.untels.biblioteca.service.PrestamoService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class PrestamoServiceImpl implements PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;
    private final UsuarioRepository usuarioRepository;
    private final MultaRepository multaRepository;
    private final PrestamoMapper prestamoMapper;

    @Value("${app.prestamo.dias-plazo:14}")
    private int diasPlazo;

    @Value("${app.multa.monto-por-dia:2.00}")
    private BigDecimal montoMultaPorDia;

    @Override
    public PrestamoResponse crear(PrestamoRequest request, String emailSolicitante) {
        Usuario solicitante = usuarioRepository.findByEmail(emailSolicitante)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + emailSolicitante));

        Usuario destinatario = solicitante;
        if (request.usuarioId() != null && !request.usuarioId().equals(solicitante.getId())) {
            if (solicitante.getRol() == Rol.ESTUDIANTE) {
                throw new AccessDeniedException("Un estudiante solo puede registrar prestamos a su propio nombre");
            }
            destinatario = usuarioRepository.findById(request.usuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + request.usuarioId()));
        }

        Libro libro = libroRepository.findById(request.libroId())
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + request.libroId()));

        if (libro.getStock() <= 0) {
            throw new BusinessRuleException("No hay stock disponible del libro: " + libro.getTitulo());
        }

        boolean prestamoDuplicado = prestamoRepository.existsByLibroIdAndUsuarioIdAndEstado(
                libro.getId(), destinatario.getId(), EstadoPrestamo.ACTIVO);
        if (prestamoDuplicado) {
            throw new BusinessRuleException(
                    "El usuario ya tiene un prestamo activo de este libro. Debe devolverlo antes de solicitar otro.");
        }

        libro.setStock(libro.getStock() - 1);
        libroRepository.save(libro);

        LocalDate hoy = LocalDate.now();
        Prestamo prestamo = Prestamo.builder()
                .libro(libro)
                .usuario(destinatario)
                .fechaPrestamo(hoy)
                .fechaDevolucionEsperada(hoy.plusDays(diasPlazo))
                .estado(EstadoPrestamo.ACTIVO)
                .build();

        return prestamoMapper.toResponse(prestamoRepository.save(prestamo));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PrestamoResponse> listar(String emailSolicitante, Pageable pageable) {
        Usuario solicitante = usuarioRepository.findByEmail(emailSolicitante)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + emailSolicitante));

        Page<Prestamo> pagina = (solicitante.getRol() == Rol.ESTUDIANTE)
                ? prestamoRepository.findByUsuarioId(solicitante.getId(), pageable)
                : prestamoRepository.findAll(pageable);

        return PageResponse.from(pagina.map(prestamoMapper::toResponse));
    }

    @Override
    public PrestamoResponse devolver(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prestamo no encontrado con id: " + id));

        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new BusinessRuleException("Este prestamo ya fue devuelto");
        }

        LocalDate hoy = LocalDate.now();
        prestamo.setFechaDevolucionReal(hoy);
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);

        Libro libro = prestamo.getLibro();
        libro.setStock(libro.getStock() + 1);
        libroRepository.save(libro);

        prestamo = prestamoRepository.save(prestamo);

        long diasAtraso = ChronoUnit.DAYS.between(prestamo.getFechaDevolucionEsperada(), hoy);
        if (diasAtraso > 0) {
            BigDecimal monto = montoMultaPorDia.multiply(BigDecimal.valueOf(diasAtraso));
            Multa multa = Multa.builder()
                    .prestamo(prestamo)
                    .monto(monto)
                    .pagada(false)
                    .fechaGeneracion(hoy)
                    .build();
            multaRepository.save(multa);
        }

        return prestamoMapper.toResponse(prestamo);
    }
}
