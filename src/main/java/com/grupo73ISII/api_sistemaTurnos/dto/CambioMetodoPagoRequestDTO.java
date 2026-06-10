package com.grupo73ISII.api_sistemaTurnos.dto;

import lombok.Data;

@Data
public class CambioMetodoPagoRequestDTO {
    private Long idFactura;
    private Long idMetodoPago;
}
