package com.grupo73ISII.api_sistemaTurnos.controller;

import com.grupo73ISII.api_sistemaTurnos.dto.CambioMetodoPagoRequestDTO;
import com.grupo73ISII.api_sistemaTurnos.dto.ProcesarPagoRequestDTO;
import com.grupo73ISII.api_sistemaTurnos.service.IFacturacionService;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/facturas")
public class FacturacionController {

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Autowired
    private IFacturacionService facturacionService;

    @PostMapping("/procesar-pago")
    public ResponseEntity<Void> procesarPagoFactura(@RequestBody ProcesarPagoRequestDTO request) {
        facturacionService.procesarFacturaPendiente(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/solicitar-reembolso")
    public ResponseEntity<Void> solicitarReembolso(@PathVariable("id") Long idFactura) {
        facturacionService.procesarDevolucion(idFactura);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cambiar-metodo")
    public ResponseEntity<Void> cambiarMetodoDePago(@RequestBody CambioMetodoPagoRequestDTO request) {
        facturacionService.procesarCambiodeMetodo(request.getIdFactura(), request.getIdMetodoPago());
        return ResponseEntity.ok().build();
    }

    //TEST DE PAGO http://localhost:8080/api/pagos/test-pago/numerotranscaccion
    @GetMapping("/test-pago/{id}")
    public String testPago(@PathVariable Long id) throws MPException, MPApiException {
        MercadoPagoConfig.setAccessToken(accessToken);
        PaymentClient client = new PaymentClient();
        Payment payment = client.get(id);
        return "Status: " + payment.getStatus() + " | Detail: " + payment.getStatusDetail();
    }
}
