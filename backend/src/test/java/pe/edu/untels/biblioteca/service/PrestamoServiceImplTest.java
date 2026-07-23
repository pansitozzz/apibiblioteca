package pe.edu.untels.biblioteca.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;
import pe.edu.untels.biblioteca.dto.request.PrestamoRequest;
import pe.edu.untels.biblioteca.dto.response.LibroResponse;
import pe.edu.untels.biblioteca.dto.response.PrestamoResponse;
import pe.edu.untels.biblioteca.dto.response.UsuarioResponse;
import pe.edu.untels.biblioteca.entity.*;
import pe.edu.untels.biblioteca.exception.BusinessRuleException;
import pe.edu.untels.biblioteca.exception.ResourceNotFoundException;
import pe.edu.untels.biblioteca.mapper.PrestamoMapper;
import pe.edu.untels.biblioteca.repository.LibroRepository;
import pe.edu.untels.biblioteca.repository.MultaRepository;
import pe.edu.untels.biblioteca.repository.PrestamoRepository;
import pe.edu.untels.biblioteca.repository.UsuarioRepository;
import pe.edu.untels.biblioteca.service.impl.PrestamoServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrestamoServiceImplTest {

    @Mock
    private PrestamoRepository prestamoRepository;
    @Mock
    private LibroRepository libroRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private MultaRepository multaRepository;
    @Mock
    private PrestamoMapper prestamoMapper;

    private PrestamoServiceImpl prestamoService;

    private Usuario estudiante;
    private Libro libro;

    @BeforeEach
    void setUp() {
        prestamoService = new PrestamoServiceImpl(
                prestamoRepository, libroRepository, usuarioRepository, multaRepository, prestamoMapper);
        ReflectionTestUtils.setField(prestamoService, "diasPlazo", 14);
        ReflectionTestUtils.setField(prestamoService, "montoMultaPorDia", new BigDecimal("2.00"));

        estudiante = Usuario.builder()
                .id(1L).nombre("Maria").apellido("Quispe").email("estudiante@biblioteca.demo")
                .password("hash").rol(Rol.ESTUDIANTE).build();

        libro = Libro.builder()
                .id(10L).titulo("Clean Code").isbn("9780132350884")
                .autor(Autor.builder().id(1L).nombre("Robert").apellido("Martin").build())
                .categoria(Categoria.builder().id(1L).nombre("Tecnología").build())
                .stock(2)
                .build();
    }

    @Test
    void deberiaCrearPrestamoYDescontarStockCuandoHayDisponibilidad() {
        PrestamoRequest request = new PrestamoRequest(libro.getId(), null);

        when(usuarioRepository.findByEmail(estudiante.getEmail())).thenReturn(Optional.of(estudiante));
        when(libroRepository.findById(libro.getId())).thenReturn(Optional.of(libro));
        when(prestamoRepository.existsByLibroIdAndUsuarioIdAndEstado(libro.getId(), estudiante.getId(), EstadoPrestamo.ACTIVO))
                .thenReturn(false);
        when(prestamoRepository.save(any(Prestamo.class))).thenAnswer(inv -> inv.getArgument(0));
        when(prestamoMapper.toResponse(any(Prestamo.class))).thenReturn(mockPrestamoResponse());

        PrestamoResponse response = prestamoService.crear(request, estudiante.getEmail());

        assertThat(response).isNotNull();
        assertThat(libro.getStock()).isEqualTo(1);

        ArgumentCaptor<Prestamo> captor = ArgumentCaptor.forClass(Prestamo.class);
        verify(prestamoRepository).save(captor.capture());
        assertThat(captor.getValue().getEstado()).isEqualTo(EstadoPrestamo.ACTIVO);
        assertThat(captor.getValue().getFechaDevolucionEsperada()).isEqualTo(LocalDate.now().plusDays(14));
    }

    @Test
    void deberiaRechazarPrestamoCuandoNoHayStockDisponible() {
        libro.setStock(0);
        PrestamoRequest request = new PrestamoRequest(libro.getId(), null);

        when(usuarioRepository.findByEmail(estudiante.getEmail())).thenReturn(Optional.of(estudiante));
        when(libroRepository.findById(libro.getId())).thenReturn(Optional.of(libro));

        assertThatThrownBy(() -> prestamoService.crear(request, estudiante.getEmail()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("stock");

        verify(prestamoRepository, never()).save(any());
    }

    @Test
    void deberiaRechazarPrestamoDuplicadoDelMismoLibroYUsuario() {
        PrestamoRequest request = new PrestamoRequest(libro.getId(), null);

        when(usuarioRepository.findByEmail(estudiante.getEmail())).thenReturn(Optional.of(estudiante));
        when(libroRepository.findById(libro.getId())).thenReturn(Optional.of(libro));
        when(prestamoRepository.existsByLibroIdAndUsuarioIdAndEstado(libro.getId(), estudiante.getId(), EstadoPrestamo.ACTIVO))
                .thenReturn(true);

        assertThatThrownBy(() -> prestamoService.crear(request, estudiante.getEmail()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("préstamo activo");

        verify(prestamoRepository, never()).save(any());
    }

    @Test
    void deberiaRechazarQueUnEstudianteRegistrePrestamoParaOtroUsuario() {
        PrestamoRequest request = new PrestamoRequest(libro.getId(), 99L);

        when(usuarioRepository.findByEmail(estudiante.getEmail())).thenReturn(Optional.of(estudiante));

        assertThatThrownBy(() -> prestamoService.crear(request, estudiante.getEmail()))
                .isInstanceOf(AccessDeniedException.class);

        verify(libroRepository, never()).findById(any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoElLibroNoExiste() {
        PrestamoRequest request = new PrestamoRequest(999L, null);

        when(usuarioRepository.findByEmail(estudiante.getEmail())).thenReturn(Optional.of(estudiante));
        when(libroRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> prestamoService.crear(request, estudiante.getEmail()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deberiaReponerStockYNoGenerarMultaCuandoLaDevolucionEsATiempo() {
        Prestamo prestamo = Prestamo.builder()
                .id(1L).libro(libro).usuario(estudiante)
                .fechaPrestamo(LocalDate.now().minusDays(5))
                .fechaDevolucionEsperada(LocalDate.now().plusDays(9))
                .estado(EstadoPrestamo.ACTIVO)
                .build();

        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamo));
        when(prestamoRepository.save(any(Prestamo.class))).thenAnswer(inv -> inv.getArgument(0));
        when(prestamoMapper.toResponse(any(Prestamo.class))).thenReturn(mockPrestamoResponse());

        prestamoService.devolver(1L);

        assertThat(libro.getStock()).isEqualTo(3);
        verify(multaRepository, never()).save(any());
    }

    @Test
    void deberiaGenerarMultaProporcionalAlosDiasDeAtrasoAlDevolver() {
        Prestamo prestamo = Prestamo.builder()
                .id(1L).libro(libro).usuario(estudiante)
                .fechaPrestamo(LocalDate.now().minusDays(20))
                .fechaDevolucionEsperada(LocalDate.now().minusDays(5))
                .estado(EstadoPrestamo.ACTIVO)
                .build();

        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamo));
        when(prestamoRepository.save(any(Prestamo.class))).thenAnswer(inv -> inv.getArgument(0));
        when(prestamoMapper.toResponse(any(Prestamo.class))).thenReturn(mockPrestamoResponse());

        prestamoService.devolver(1L);

        ArgumentCaptor<Multa> captor = ArgumentCaptor.forClass(Multa.class);
        verify(multaRepository).save(captor.capture());
        assertThat(captor.getValue().getMonto()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(prestamo.getEstado()).isEqualTo(EstadoPrestamo.DEVUELTO);
    }

    @Test
    void deberiaRechazarDevolverUnPrestamoYaDevuelto() {
        Prestamo prestamo = Prestamo.builder()
                .id(1L).libro(libro).usuario(estudiante)
                .fechaPrestamo(LocalDate.now().minusDays(20))
                .fechaDevolucionEsperada(LocalDate.now().minusDays(5))
                .fechaDevolucionReal(LocalDate.now().minusDays(3))
                .estado(EstadoPrestamo.DEVUELTO)
                .build();

        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamo));

        assertThatThrownBy(() -> prestamoService.devolver(1L))
                .isInstanceOf(BusinessRuleException.class);

        verify(libroRepository, never()).save(any());
    }

    @Test
    void deberiaListarSoloLosPrestamosDelEstudianteAutenticado() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Prestamo> pagina = new PageImpl<>(List.of());

        when(usuarioRepository.findByEmail(estudiante.getEmail())).thenReturn(Optional.of(estudiante));
        when(prestamoRepository.findByUsuarioId(estudiante.getId(), pageable)).thenReturn(pagina);

        prestamoService.listar(estudiante.getEmail(), pageable);

        verify(prestamoRepository).findByUsuarioId(estudiante.getId(), pageable);
        verify(prestamoRepository, never()).findAll(pageable);
    }

    private PrestamoResponse mockPrestamoResponse() {
        return new PrestamoResponse(1L, mock(LibroResponse.class), mock(UsuarioResponse.class),
                LocalDate.now(), LocalDate.now().plusDays(14), null, EstadoPrestamo.ACTIVO);
    }
}
