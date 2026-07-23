package pe.edu.untels.biblioteca.service;

import pe.edu.untels.biblioteca.dto.request.LoginRequest;
import pe.edu.untels.biblioteca.dto.request.RegistroRequest;
import pe.edu.untels.biblioteca.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse registrar(RegistroRequest request);

    AuthResponse login(LoginRequest request);
}
