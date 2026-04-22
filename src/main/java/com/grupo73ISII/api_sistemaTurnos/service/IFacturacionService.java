package com.grupo73ISII.api_sistemaTurnos.service;

import com.grupo73ISII.api_sistemaTurnos.model.Facturacion;
import java.math.BigDecimal;
import java.util.Optional;

public interface IFacturacionService {
    Facturacion inicializarPago(Long idMetodoPago, BigDecimal valorMonetario);
    Optional<Facturacion> findById(Long id);
    boolean estaPagada(Long idFacturacion);
}
