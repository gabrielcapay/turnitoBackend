package com.grupo73ISII.api_sistemaTurnos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaDetalleDTO {
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
}
