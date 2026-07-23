package pe.edu.untels.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "multas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Multa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prestamo_id", nullable = false, unique = true)
    private Prestamo prestamo;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private boolean pagada;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDate fechaGeneracion;
}
