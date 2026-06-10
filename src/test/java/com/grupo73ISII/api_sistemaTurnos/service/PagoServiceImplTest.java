package com.grupo73ISII.api_sistemaTurnos.service;

import com.grupo73ISII.api_sistemaTurnos.model.Facturacion;
import com.grupo73ISII.api_sistemaTurnos.model.MetodoDePago;
import com.grupo73ISII.api_sistemaTurnos.model.Pago;
import com.grupo73ISII.api_sistemaTurnos.repository.FacturacionRepository;
import com.grupo73ISII.api_sistemaTurnos.repository.PagoRepository;
import com.grupo73ISII.api_sistemaTurnos.service.impl.PagoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceImplTest {

    @Mock private PagoRepository pagoRepository;
    @Mock private FacturacionRepository facturacionRepository;
    @Mock private Map<String, IMetodoDePago> metodosDePago;

    @InjectMocks
    private PagoServiceImpl pagoService;

    private Pago buildPago(Long id, boolean pagado, String metodDescripcion) {
        MetodoDePago metodo = MetodoDePago.builder()
                .idMetodoPago(2L)
                .descripcion(metodDescripcion)
                .build();
        return Pago.builder()
                .idPago(id)
                .estado(pagado)
                .monto(1000.0)
                .metodoDePago(metodo)
                .build();
    }

    // CASO 1: Datos válidos → pago aprobado
    @Test
    void procesarPago_datosValidos_procesaCorrectamente() {
        Pago pago = buildPago(10L, false, "mercadopago");
        Facturacion facturacion = Facturacion.builder().idFacturacion(1L).build();
        IMetodoDePago estrategia = mock(IMetodoDePago.class);

        when(pagoRepository.findById(10L)).thenReturn(Optional.of(pago));
        when(facturacionRepository.findByPagoIdPago(10L)).thenReturn(Optional.of(facturacion));
        when(metodosDePago.get("mercadopago")).thenReturn(estrategia);
        when(estrategia.ejecutarPago(anyDouble(), anyLong(), anyString(), anyString(), anyString())).thenReturn("TXN-123");
        when(pagoRepository.save(any())).thenReturn(pago);

        assertDoesNotThrow(() ->
            pagoService.procesarPago("tok_abc123", "2", "cliente@mail.com", 10L)
        );

        verify(estrategia).ejecutarPago(anyDouble(), anyLong(), eq("tok_abc123"), eq("cliente@mail.com"), eq("2"));
    }

    // CASO 2: Método de pago rechaza el cobro (fondos insuficientes)
    @Test
    void procesarPago_metodoPagoRechaza_lanzaExcepcion() {
        Pago pago = buildPago(10L, false, "mercadopago");
        Facturacion facturacion = Facturacion.builder().idFacturacion(1L).build();
        IMetodoDePago estrategia = mock(IMetodoDePago.class);

        when(pagoRepository.findById(10L)).thenReturn(Optional.of(pago));
        when(facturacionRepository.findByPagoIdPago(10L)).thenReturn(Optional.of(facturacion));
        when(metodosDePago.get("mercadopago")).thenReturn(estrategia);
        when(estrategia.ejecutarPago(anyDouble(), anyLong(), anyString(), anyString(), anyString()))
            .thenThrow(new RuntimeException("Fondos insuficientes"));

        assertThrows(RuntimeException.class, () ->
            pagoService.procesarPago("tok_tarjeta_invalida", "2", "cliente@mail.com", 10L)
        );
    }

    // CASO 3: Token inválido (MP rechaza)
    @Test
    void procesarPago_tokenInvalido_lanzaExcepcion() {
        Pago pago = buildPago(10L, false, "mercadopago");
        Facturacion facturacion = Facturacion.builder().idFacturacion(1L).build();
        IMetodoDePago estrategia = mock(IMetodoDePago.class);

        when(pagoRepository.findById(10L)).thenReturn(Optional.of(pago));
        when(facturacionRepository.findByPagoIdPago(10L)).thenReturn(Optional.of(facturacion));
        when(metodosDePago.get("mercadopago")).thenReturn(estrategia);
        when(estrategia.ejecutarPago(anyDouble(), anyLong(), anyString(), anyString(), anyString()))
            .thenThrow(new RuntimeException("Token inválido"));

        assertThrows(RuntimeException.class, () ->
            pagoService.procesarPago("tok_tarjeta_invalida", "2", "cliente@mail.com", 10L)
        );
    }

    // CASO 4: La factura ya estaba pagada
    @Test
    void procesarPago_facturaYaPagada_lanzaExcepcion() {
        Pago pago = buildPago(10L, true, "mercadopago");

        when(pagoRepository.findById(10L)).thenReturn(Optional.of(pago));

        assertThrows(RuntimeException.class, () ->
            pagoService.procesarPago("tok_abc123", "2", "cliente@mail.com", 10L)
        );
    }

    // CASO 5: Token vacío
    @Test
    void procesarPago_tokenVacio_lanzaExcepcion() {
        assertThrows(RuntimeException.class, () ->
            pagoService.procesarPago("", "2", "cliente@mail.com", 10L)
        );
    }

    // CASO 6: Email con formato inválido
    @Test
    void procesarPago_emailInvalido_lanzaExcepcion() {
        assertThrows(RuntimeException.class, () ->
            pagoService.procesarPago("tok_abc123", "2", "correo-invalido", 10L)
        );
    }

    // CASO 7: metodoPagoId nulo
    @Test
    void procesarPago_metodoPagoNulo_lanzaExcepcion() {
        assertThrows(RuntimeException.class, () ->
            pagoService.procesarPago("tok_abc123", null, "cliente@mail.com", 10L)
        );
    }

    // CASO 8: idPago nulo
    @Test
    void procesarPago_idPagoNulo_lanzaExcepcion() {
        assertThrows(RuntimeException.class, () ->
            pagoService.procesarPago("tok_abc123", "2", "cliente@mail.com", null)
        );
    }

    // CASO 9: token nulo
    @Test
    void procesarPago_tokenNulo_lanzaExcepcion() {
        assertThrows(RuntimeException.class, () ->
            pagoService.procesarPago(null, "2", "cliente@mail.com", 10L)
        );
    }

    // CASO 10: email nulo
    @Test
    void procesarPago_emailNulo_lanzaExcepcion() {
        assertThrows(RuntimeException.class, () ->
            pagoService.procesarPago("tok_abc123", "2", null, 10L)
        );
    }
}
