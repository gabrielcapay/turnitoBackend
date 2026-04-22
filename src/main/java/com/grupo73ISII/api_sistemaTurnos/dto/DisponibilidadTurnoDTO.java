package com.grupo73ISII.api_sistemaTurnos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisponibilidadTurnoDTO {
    private String idFranjaHoraria;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String estado;
}
