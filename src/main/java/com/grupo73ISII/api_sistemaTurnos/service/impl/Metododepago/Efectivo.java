package com.grupo73ISII.api_sistemaTurnos.service.impl.Metododepago;

import com.grupo73ISII.api_sistemaTurnos.service.IMetodoDePago;
import org.springframework.stereotype.Component;

@Component("efectivo")
public class Efectivo implements IMetodoDePago {

    @Override
    public String ejecutarPago(double monto, Long idFactura, String datosTransaccion, String email) {
        // Solo registro, sin llamadas externas
        // Genera un número de operación interno
        return "EF-" + System.currentTimeMillis();
    }

    @Override
    public void ejecutarDevolucion(String referencia) {
        // Solo registro, la devolución física la hace el recepcionista
    }
}
