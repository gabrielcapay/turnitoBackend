package com.grupo73ISII.api_sistemaTurnos.service;

import com.grupo73ISII.api_sistemaTurnos.model.Pago;

import java.util.Optional;

public interface IPagoService {
    Pago save(Pago pago);
    Optional<Pago> findById(Long id);
    void procesarPago(String token, String paymentMethodId, String email, Long idPago);
    Pago actualizarMetodoPago(Long idPago, Long idMetodoNuevo);
    Pago registrarElPago(Long idPago, String numeroTransaccion);
    void generarDevolucion(Long idPago);
    void validarPeriodoDevolucion(Long idPago);
    Pago registrarDevolucion(Long idPago);
}
