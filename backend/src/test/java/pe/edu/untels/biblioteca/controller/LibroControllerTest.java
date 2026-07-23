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
import pe.edu.untels.biblioteca.dto.request.LibroRequest;
import pe.edu.untels.biblioteca.entity.Autor;
import pe.edu.untels.biblioteca.entity.Categoria;
import pe.edu.untels.biblioteca.entity.Libro;
import pe.edu.untels.biblioteca.entity.Rol;
import pe.edu.untels.biblioteca.entity.Usuario;
import pe.edu.untels.biblioteca.repository.AutorRepository;
import pe.edu.untels.biblioteca.repository.CategoriaRepository;
import pe.edu.untels.biblioteca.repository.LibroRepository;
import pe.edu.untels.biblioteca.repository.UsuarioRepository;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LibroControllerTest {

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

    private Long autorId;
    private Long categoriaId;
    private String tokenBibliotecario;
    private String tokenEstudiante;

    @BeforeEach
    void setUp() {
        Autor autor = autorRepository.save(Autor.builder()
                .nombre("Robert").apellido("Martin").nacionalidad("Estadounidense").build());
        autorId = autor.getId();

        Categoria categoria = categoriaRepository.save(Categoria.builder()
                .nombre("Tecnología-" + System.nanoTime()).descripcion("Libros técnicos").build());
        categoriaId = categoria.getId();

        libroRepository.save(Libro.builder()
                .titulo("Refactoring").isbn("ISBN-" + (System.nanoTime() % 1000000))
                .autor(autor).categoria(categoria).stock(3).anioPublicacion(1999).build());

        Usuario bibliotecario = usuarioRepository.save(Usuario.builder()
                .nombre("Luis").apellido("Ramirez")
                .email("bibliotecario-" + System.nanoTime() + "@correo.demo")
                .password(passwordEncoder.encode("ClaveSegura123"))
                .rol(Rol.BIBLIOTECARIO).build());

        Usuario estudiante = usuarioRepository.save(Usuario.builder()
                .nombre("Maria").apellido("Quispe")
                .email("estudiante-" + System.nanoTime() + "@correo.demo")
                .password(passwordEncoder.encode("ClaveSegura123"))
                .rol(Rol.ESTUDIANTE).build());

        tokenBibliotecario = login(bibliotecario.getEmail());
        tokenEstudiante = login(estudiante.getEmail());
    }

    private String login(String email) {
        try {
            String body = objectMapper.writeValueAsString(
                    new pe.edu.untels.biblioteca.dto.request.LoginRequest(email, "ClaveSegura123"));
            String response = mockMvc.perform(post("/api/v1/auth/login")
                            .contentType("application/json").content(body))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            return objectMapper.readTree(response).get("token").asText();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deberiaListarLibrosPaginados() throws Exception {
        mockMvc.perform(get("/api/v1/libros")
                        .header("Authorization", "Bearer " + tokenEstudiante))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido").isArray());
    }

    @Test
    void deberiaRechazarListarSinAutenticacion() throws Exception {
        mockMvc.perform(get("/api/v1/libros"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deberiaPermitirQueBibliotecarioCreeUnLibro() throws Exception {
        LibroRequest request = new LibroRequest(
                "Domain-Driven Design", "ISBN-" + (System.nanoTime() % 1000000), autorId, categoriaId, 2, 2003, "Addison-Wesley", null);

        mockMvc.perform(post("/api/v1/libros")
                        .header("Authorization", "Bearer " + tokenBibliotecario)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo", is("Domain-Driven Design")));
    }

    @Test
    void deberiaRechazarQueUnEstudianteCreeUnLibro() throws Exception {
        LibroRequest request = new LibroRequest(
                "Otro Libro", "ISBN-" + (System.nanoTime() % 1000000), autorId, categoriaId, 1, 2020, "Editorial X", null);

        mockMvc.perform(post("/api/v1/libros")
                        .header("Authorization", "Bearer " + tokenEstudiante)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deberiaRechazarCrearLibroConIsbnDuplicado() throws Exception {
        String isbnRepetido = "ISBN-DUP-" + (System.nanoTime() % 100000);
        LibroRequest primero = new LibroRequest(
                "Libro Uno", isbnRepetido, autorId, categoriaId, 1, 2020, "Editorial X", null);
        LibroRequest segundo = new LibroRequest(
                "Libro Dos", isbnRepetido, autorId, categoriaId, 1, 2021, "Editorial Y", null);

        mockMvc.perform(post("/api/v1/libros")
                        .header("Authorization", "Bearer " + tokenBibliotecario)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(primero)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/libros")
                        .header("Authorization", "Bearer " + tokenBibliotecario)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(segundo)))
                .andExpect(status().isConflict());
    }
}
