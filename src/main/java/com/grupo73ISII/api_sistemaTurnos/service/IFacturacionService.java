package com.grupo73ISII.api_sistemaTurnos.service;

import com.grupo73ISII.api_sistemaTurnos.dto.ProcesarPagoRequestDTO;
import com.grupo73ISII.api_sistemaTurnos.model.Facturacion;
import java.math.BigDecimal;
import java.util.Optional;

public interface IFacturacionService {
    Facturacion inicializarPago(Long idMetodoPago, BigDecimal valorMonetario);
    Optional<Facturacion> findById(Long id);
    boolean estaPagada(Long idFacturacion);
    void procesarFacturaPendiente(ProcesarPagoRequestDTO request);
    void actualizarFacturaAPagado(Long idFactura);
    void procesarDevolucion(Long idFactura);
    void actualizarFacturaADevuelto(Long idFactura);
    void actualizarFacturaADevolucionPendiente(Long idFactura);
    void procesarCambiodeMetodo(Long idFactura, Long idMetodoPago);
}
