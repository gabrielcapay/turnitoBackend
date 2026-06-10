package com.grupo73ISII.api_sistemaTurnos.service.impl.Metododepago;

import com.grupo73ISII.api_sistemaTurnos.service.IMetodoDePago;
import org.springframework.stereotype.Component;

@Component("efectivo")
public class Efectivo implements IMetodoDePago {

    @Override
    public String ejecutarPago(double monto, Long idFactura, String datosTransaccion, String email, String paymentMethodId) {
        // Solo registro, sin llamadas externas
        // Genera el número de referencia interno del pago en efectivo
        return "EFE" + idFactura;
    }

    @Override
    public void ejecutarDevolucion(String referencia) {
        // Solo registro, la devolución física la hace el recepcionista
    }
}
