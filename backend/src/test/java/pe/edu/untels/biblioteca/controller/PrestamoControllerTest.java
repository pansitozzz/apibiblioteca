package pe.edu.untels.biblioteca.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.untels.biblioteca.dto.request.LoginRequest;
import pe.edu.untels.biblioteca.dto.request.PrestamoRequest;
import pe.edu.untels.biblioteca.entity.*;
import pe.edu.untels.biblioteca.repository.*;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PrestamoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AutorRepository autorRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private LibroRepository libroRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Libro libroConStock;
    private Libro libroSinStock;
    private String tokenEstudiante;

    @BeforeEach
    void setUp() throws Exception {
        Autor autor = autorRepository.save(Autor.builder().nombre("Autor").apellido("Prueba").build());
        Categoria categoria = categoriaRepository.save(
                Categoria.builder().nombre("Cat-" + System.nanoTime()).build());

        libroConStock = libroRepository.save(Libro.builder()
                .titulo("Libro con stock").isbn("ISBN-STK-" + (System.nanoTime() % 100000))
                .autor(autor).categoria(categoria).stock(1).build());

        libroSinStock = libroRepository.save(Libro.builder()
                .titulo("Libro sin stock").isbn("ISBN-NST-" + (System.nanoTime() % 100000))
                .autor(autor).categoria(categoria).stock(0).build());

        Usuario estudiante = usuarioRepository.save(Usuario.builder()
                .nombre("Maria").apellido("Quispe")
                .email("estudiante-prestamo-" + System.nanoTime() + "@correo.demo")
                .password(passwordEncoder.encode("ClaveSegura123"))
                .rol(Rol.ESTUDIANTE).build());

        String body = objectMapper.writeValueAsString(new LoginRequest(estudiante.getEmail(), "ClaveSegura123"));
        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json").content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        tokenEstudiante = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void deberiaCrearPrestamoYDescontarStock() throws Exception {
        PrestamoRequest request = new PrestamoRequest(libroConStock.getId(), null);

        mockMvc.perform(post("/api/v1/prestamos")
                        .header("Authorization", "Bearer " + tokenEstudiante)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado", is("ACTIVO")));

        Libro actualizado = libroRepository.findById(libroConStock.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(actualizado.getStock()).isEqualTo(0);
    }

    @Test
    void deberiaRechazarPrestamoSinStockDisponible() throws Exception {
        PrestamoRequest request = new PrestamoRequest(libroSinStock.getId(), null);

        mockMvc.perform(post("/api/v1/prestamos")
                        .header("Authorization", "Bearer " + tokenEstudiante)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void deberiaRechazarSegundoPrestamoActivoDelMismoLibro() throws Exception {
        PrestamoRequest request = new PrestamoRequest(libroConStock.getId(), null);
        libroConStock.setStock(2);
        libroRepository.save(libroConStock);

        mockMvc.perform(post("/api/v1/prestamos")
                        .header("Authorization", "Bearer " + tokenEstudiante)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/prestamos")
                        .header("Authorization", "Bearer " + tokenEstudiante)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void deberiaRechazarDevolucionSiElUsuarioNoEsBibliotecarioOAdmin() throws Exception {
        mockMvc.perform(put("/api/v1/prestamos/1/devolver")
                        .header("Authorization", "Bearer " + tokenEstudiante))
                .andExpect(status().isForbidden());
    }
}
