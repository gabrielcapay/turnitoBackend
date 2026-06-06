package com.grupo73ISII.api_sistemaTurnos.service.impl.Metododepago;

import com.grupo73ISII.api_sistemaTurnos.service.IMetodoDePago;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.client.payment.PaymentRefundClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("mercadoPago")
public class MercadoPago implements IMetodoDePago {

    @Value("${mercadopago.notification-url}")
    private String notificationUrl;

    @Override
    public String ejecutarPago(double monto, Long idFactura, String datosTransaccion, String email) {
        try {
            PaymentClient client = new PaymentClient();

            PaymentCreateRequest request = PaymentCreateRequest.builder()
                .transactionAmount(new BigDecimal(monto))
                .token(datosTransaccion)               // cardToken generado por MP en el frontend
                .installments(1)
                .payer(PaymentPayerRequest.builder()
                    .email(email)
                    .build())
                .externalReference(String.valueOf(idFactura))
                .notificationUrl(notificationUrl)
                .build();

            Payment payment = client.create(request);
            return String.valueOf(payment.getId());

        } catch (MPApiException e) {
            throw new RuntimeException("Error en la API de MercadoPago: " + e.getApiResponse().getContent(), e);
        } catch (MPException e) {
            throw new RuntimeException("Error al conectar con MercadoPago: " + e.getMessage(), e);
        }
    }

    @Override
    public void ejecutarDevolucion(String referencia) {
        try {
            PaymentRefundClient client = new PaymentRefundClient();
            client.refund(Long.parseLong(referencia));
        } catch (MPApiException e) {
            throw new RuntimeException("Error en la API de MercadoPago: " + e.getApiResponse().getContent(), e);
        } catch (MPException e) {
            throw new RuntimeException("Error al conectar con MercadoPago: " + e.getMessage(), e);
        }
    }
}
