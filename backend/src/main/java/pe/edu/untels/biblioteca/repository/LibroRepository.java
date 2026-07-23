package pe.edu.untels.biblioteca.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.untels.biblioteca.entity.Libro;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    boolean existsByIsbn(String isbn);

    @Query("""
            SELECT l FROM Libro l
            WHERE (:titulo IS NULL OR LOWER(l.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')))
            AND (:autor IS NULL OR LOWER(CONCAT(l.autor.nombre, ' ', l.autor.apellido)) LIKE LOWER(CONCAT('%', :autor, '%')))
            AND (:categoriaId IS NULL OR l.categoria.id = :categoriaId)
            """)
    Page<Libro> buscar(@Param("titulo") String titulo,
                        @Param("autor") String autor,
                        @Param("categoriaId") Long categoriaId,
                        Pageable pageable);
}
