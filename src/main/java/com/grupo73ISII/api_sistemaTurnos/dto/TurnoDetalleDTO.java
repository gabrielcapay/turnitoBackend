package com.grupo73ISII.api_sistemaTurnos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoDetalleDTO {
    private Long idTurno;
    private Long idCliente;
    private LocalDate fechaDeTurno;
    private String estadoTurno;
    private ReservaDetalleDTO reserva;
}
