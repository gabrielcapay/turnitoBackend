package com.grupo73ISII.api_sistemaTurnos.service;

public interface IMetodoDePago {
    String ejecutarPago(double monto, Long idFactura, String datosTransaccion, String email);
    void ejecutarDevolucion(String referencia);
}
