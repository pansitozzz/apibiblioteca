package pe.edu.untels.biblioteca.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MultaResponse(
        Long id,
        PrestamoResponse prestamo,
        BigDecimal monto,
        boolean pagada,
        LocalDate fechaGeneracion
) {
}
