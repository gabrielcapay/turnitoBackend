package com.grupo73ISII.api_sistemaTurnos.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TurnoRequestDTO {
    private Long idTurno;
    private Long idCliente;
    private LocalDate fechaTurno;
    private ReservaRequestDTO reservaRequest;
}
