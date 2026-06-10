package com.grupo73ISII.api_sistemaTurnos.dto;

import lombok.Data;

@Data
public class ProcesarPagoRequestDTO {
    private String token;
    private String paymentMethodId;
    private String email;
    private Long idFactura;
}
