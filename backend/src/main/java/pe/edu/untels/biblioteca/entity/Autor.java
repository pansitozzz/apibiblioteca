package pe.edu.untels.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "autores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(length = 60)
    private String nacionalidad;

    @Column(columnDefinition = "TEXT")
    private String biografia;
}
