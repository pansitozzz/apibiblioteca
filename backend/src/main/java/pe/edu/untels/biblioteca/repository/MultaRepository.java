package pe.edu.untels.biblioteca.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.untels.biblioteca.entity.Multa;

public interface MultaRepository extends JpaRepository<Multa, Long> {

    Page<Multa> findByPrestamoUsuarioId(Long usuarioId, Pageable pageable);
}
