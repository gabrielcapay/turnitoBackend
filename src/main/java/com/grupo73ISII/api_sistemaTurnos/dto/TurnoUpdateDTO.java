package com.grupo73ISII.api_sistemaTurnos.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TurnoUpdateDTO {
    private LocalDate nuevaFechaTurno;
    private String nuevoIdFranjaHoraria;
    private Long nuevoIdCancha;
}
