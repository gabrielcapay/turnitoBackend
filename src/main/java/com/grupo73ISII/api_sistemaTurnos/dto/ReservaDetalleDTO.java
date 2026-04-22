package com.grupo73ISII.api_sistemaTurnos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDetalleDTO {
    private BigDecimal monto;
    private FacturacionDetalleDTO facturacion;
}
