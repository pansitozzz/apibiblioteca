package pe.edu.untels.biblioteca.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pe.edu.untels.biblioteca.entity.Usuario;

/**
 * Utilidad expuesta a las expresiones @PreAuthorize para validar que el
 * usuario autenticado es el mismo recurso que intenta consultar o modificar.
 */
@Component("seguridadUtil")
public class SeguridadUtil {

    public boolean esUsuarioActual(Long usuarioId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario usuario)) {
            return false;
        }
        return usuario.getId().equals(usuarioId);
    }
}
