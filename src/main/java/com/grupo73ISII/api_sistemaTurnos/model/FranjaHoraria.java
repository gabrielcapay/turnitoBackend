package com.grupo73ISII.api_sistemaTurnos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import java.time.LocalTime;

@Entity
@Data
public class FranjaHoraria {

    @Id
    private String id_franjaHoraria;
    
    private LocalTime horaInicio;
    private LocalTime horaFin;

}
