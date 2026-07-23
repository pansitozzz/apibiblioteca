package pe.edu.untels.biblioteca.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.untels.biblioteca.dto.request.LoginRequest;
import pe.edu.untels.biblioteca.dto.request.RegistroRequest;
import pe.edu.untels.biblioteca.dto.response.AuthResponse;
import pe.edu.untels.biblioteca.entity.Rol;
import pe.edu.untels.biblioteca.entity.Usuario;
import pe.edu.untels.biblioteca.exception.BusinessRuleException;
import pe.edu.untels.biblioteca.exception.ResourceNotFoundException;
import pe.edu.untels.biblioteca.mapper.UsuarioMapper;
import pe.edu.untels.biblioteca.repository.UsuarioRepository;
import pe.edu.untels.biblioteca.security.JwtService;
import pe.edu.untels.biblioteca.service.AuthService;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Ya existe una cuenta registrada con el email: " + request.email());
        }

        // El auto-registro publico siempre crea cuentas ESTUDIANTE.
        // Las cuentas ADMIN/BIBLIOTECARIO se crean por un ADMIN existente (fuera del alcance de este endpoint).
        Usuario usuario = Usuario.builder()
                .nombre(request.nombre())
                .apellido(request.apellido())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .rol(Rol.ESTUDIANTE)
                .codigoEstudiante(request.codigoEstudiante())
                .build();

        usuario = usuarioRepository.save(usuario);
        String token = jwtService.generarToken(usuario);
        return new AuthResponse(token, usuarioMapper.toResponse(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + request.email()));

        String token = jwtService.generarToken(usuario);
        return new AuthResponse(token, usuarioMapper.toResponse(usuario));
    }
}
