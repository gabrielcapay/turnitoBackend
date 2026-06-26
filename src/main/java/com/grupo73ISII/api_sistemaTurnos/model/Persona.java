package com.grupo73ISII.api_sistemaTurnos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "persona")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dato_persona")
    private Long idDatoPersona;

    private String nombre;
    private String apellido;
    private String telefono;
    private String email;

    @Column(name = "fecha_de_registro")
    private LocalDateTime fechaDeRegistro;
}
