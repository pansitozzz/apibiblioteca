package pe.edu.untels.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.untels.biblioteca.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
