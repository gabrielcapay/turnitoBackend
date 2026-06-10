package com.grupo73ISII.api_sistemaTurnos.service.impl.Metododepago;

import com.grupo73ISII.api_sistemaTurnos.service.IMetodoDePago;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
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

@Component("mercadopago")
public class MercadoPago implements IMetodoDePago {

    @Value("${mercadopago.notification-url}")
    private String notificationUrl;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Override
    public String ejecutarPago(double monto, Long idFactura, String datosTransaccion, String email, String paymentMethodId) {

        try {
            MercadoPagoConfig.setAccessToken(accessToken);
            PaymentClient client = new PaymentClient();

            PaymentPayerRequest payer = PaymentPayerRequest.builder()
                .email(email)
                .identification(IdentificationRequest.builder()
                    .type("DNI")
                    .number("12345678909")
                    .build())
                .build();

            PaymentCreateRequest request = PaymentCreateRequest.builder()
                .transactionAmount(BigDecimal.valueOf(monto))
                .token(datosTransaccion)
                .description("Pago turno #" + idFactura)
                .paymentMethodId(paymentMethodId)
                .installments(1)
                    .notificationUrl(notificationUrl)
                    .externalReference(String.valueOf(idFactura))
                .payer(payer)
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
