package pe.edu.untels.biblioteca.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pe.edu.untels.biblioteca.security.JwtAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    private static final String[] RUTAS_PUBLICAS = {
            "/api/v1/auth/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/actuator/health"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(RUTAS_PUBLICAS).permitAll()

                        // Lectura de catalogo: cualquier usuario autenticado
                        .requestMatchers(HttpMethod.GET, "/api/v1/libros/**", "/api/v1/autores/**", "/api/v1/categorias/**")
                        .authenticated()

                        // Escritura de catalogo: solo ADMIN y BIBLIOTECARIO
                        .requestMatchers(HttpMethod.POST, "/api/v1/libros/**", "/api/v1/autores/**", "/api/v1/categorias/**")
                        .hasAnyRole("ADMIN", "BIBLIOTECARIO")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/libros/**", "/api/v1/autores/**", "/api/v1/categorias/**")
                        .hasAnyRole("ADMIN", "BIBLIOTECARIO")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/libros/**", "/api/v1/autores/**", "/api/v1/categorias/**")
                        .hasAnyRole("ADMIN", "BIBLIOTECARIO")

                        // Gestion de usuarios: solo ADMIN (excepto ver/editar el propio perfil, controlado en el service)
                        .requestMatchers("/api/v1/usuarios/**").authenticated()

                        // Devoluciones: solo ADMIN/BIBLIOTECARIO
                        .requestMatchers(HttpMethod.PUT, "/api/v1/prestamos/*/devolver")
                        .hasAnyRole("ADMIN", "BIBLIOTECARIO")

                        // Pago de multas: solo ADMIN/BIBLIOTECARIO
                        .requestMatchers(HttpMethod.PUT, "/api/v1/multas/*/pagar")
                        .hasAnyRole("ADMIN", "BIBLIOTECARIO")

                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
