package pe.edu.untels.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.untels.biblioteca.entity.Autor;

public interface AutorRepository extends JpaRepository<Autor, Long> {
}
