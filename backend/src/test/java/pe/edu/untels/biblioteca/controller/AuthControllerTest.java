package pe.edu.untels.biblioteca.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.untels.biblioteca.dto.request.LoginRequest;
import pe.edu.untels.biblioteca.dto.request.RegistroRequest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deberiaRegistrarUnEstudianteYDevolverToken() throws Exception {
        RegistroRequest request = new RegistroRequest(
                "Carlos", "Fernandez", "carlos.fernandez@correo.demo", "ClaveSegura123", "2022100456");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.usuario.email", is("carlos.fernandez@correo.demo")))
                .andExpect(jsonPath("$.usuario.rol", is("ESTUDIANTE")));
    }

    @Test
    void deberiaRechazarRegistroConEmailDuplicado() throws Exception {
        RegistroRequest request = new RegistroRequest(
                "Duplicado", "Prueba", "duplicado@correo.demo", "ClaveSegura123", null);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void deberiaRechazarRegistroConDatosInvalidos() throws Exception {
        RegistroRequest request = new RegistroRequest("", "", "no-es-un-email", "123", null);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles", notNullValue()));
    }

    @Test
    void deberiaHacerLoginConCredencialesValidas() throws Exception {
        RegistroRequest registro = new RegistroRequest(
                "Login", "Test", "login.test@correo.demo", "ClaveSegura123", null);
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().isCreated());

        LoginRequest login = new LoginRequest("login.test@correo.demo", "ClaveSegura123");
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    void deberiaRechazarLoginConCredencialesInvalidas() throws Exception {
        LoginRequest login = new LoginRequest("inexistente@correo.demo", "ClaveIncorrecta1");
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }
}
