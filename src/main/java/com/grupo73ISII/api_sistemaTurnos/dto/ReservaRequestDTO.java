package com.grupo73ISII.api_sistemaTurnos.dto;

import lombok.Data;

@Data
public class ReservaRequestDTO {
    private Long idCancha;
    private String idFranjaHoraria;
    private Long idMetodoDePago;
}
