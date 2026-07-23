package pe.edu.untels.biblioteca.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.untels.biblioteca.entity.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    Page<Reserva> findByUsuarioId(Long usuarioId, Pageable pageable);
}
