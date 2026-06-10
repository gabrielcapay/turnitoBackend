package com.grupo73ISII.api_sistemaTurnos.dto;

import com.grupo73ISII.api_sistemaTurnos.model.Reserva;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoResponseDTO {
    private Long idTurno;
    private LocalDate fechaTurno;
    private Boolean estadoTurno;
    private ClienteSimplificadoDTO cliente;
    private Reserva reserva; // Por simplicidad, mantenemos la entidad Reserva completa. Se puede optimizar luego.
}
