package com.grupo73ISII.api_sistemaTurnos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDetalleDTO {
    private Long idCliente;
    private Integer inasistencias;
    private Boolean suspendido;
    private PersonaDetalleDTO persona;
}
