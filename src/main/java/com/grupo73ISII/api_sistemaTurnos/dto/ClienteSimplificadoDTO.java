package com.grupo73ISII.api_sistemaTurnos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteSimplificadoDTO {
    private Long id;
    private String nombre;
    private String apellido;
}
