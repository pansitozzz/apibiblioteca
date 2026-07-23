package pe.edu.untels.biblioteca.dto.response;

public record AuthResponse(
        String token,
        String tipo,
        UsuarioResponse usuario
) {
    public AuthResponse(String token, UsuarioResponse usuario) {
        this(token, "Bearer", usuario);
    }
}
