package com.grupo73ISII.api_sistemaTurnos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import java.time.LocalTime;

@Entity
@Data
public class FranjaHoraria {

    @Id
    @Column(name = "id_franja_horaria")
    private String id_franja;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fin")
    private LocalTime horaFin;

}
