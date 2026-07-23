package pe.edu.untels.biblioteca.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.untels.biblioteca.entity.EstadoPrestamo;
import pe.edu.untels.biblioteca.entity.Prestamo;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    Page<Prestamo> findByUsuarioId(Long usuarioId, Pageable pageable);

    boolean existsByLibroIdAndUsuarioIdAndEstado(Long libroId, Long usuarioId, EstadoPrestamo estado);
}
