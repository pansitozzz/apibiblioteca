package pe.edu.untels.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.untels.biblioteca.entity.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
}
