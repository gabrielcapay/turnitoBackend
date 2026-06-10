package com.grupo73ISII.api_sistemaTurnos.service;

import com.grupo73ISII.api_sistemaTurnos.model.Facturacion;
import com.grupo73ISII.api_sistemaTurnos.model.Pago;
import com.grupo73ISII.api_sistemaTurnos.model.Turno;
import com.grupo73ISII.api_sistemaTurnos.repository.FacturacionRepository;
import com.grupo73ISII.api_sistemaTurnos.service.impl.FacturacionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DevolucionServiceImplTest {

    @Mock private FacturacionRepository facturacionRepository;
    @Mock private IPagoService pagoService;
    @Mock private IEstadoFacturacionService estadoFacturacionService;
    @Mock private IMetodoDePagoService metodoDePagoService;
    @Mock private ITurnoService turnoService;

    @InjectMocks
    private FacturacionServiceImpl facturacionService;

    private Facturacion buildFacturacion(Long idFactura, Long estadoId, Long idPago) {
        com.grupo73ISII.api_sistemaTurnos.model.EstadoFacturacion estado =
            com.grupo73ISII.api_sistemaTurnos.model.EstadoFacturacion.builder()
                .idEstadoFacturacion(estadoId).build();
        Pago pago = Pago.builder().idPago(idPago).build();
        return Facturacion.builder()
                .idFacturacion(idFactura)
                .estadoFacturacion(estado)
                .pago(pago)
                .build();
    }

    // CASO 1: Datos válidos → devolución confirmada
    @Test
    void procesarDevolucion_datosValidos_devuelveCorrectamente() {
        Facturacion facturacion = buildFacturacion(5L, 2L, 10L);
        Turno turno = Turno.builder().idTurno(1L).estadoTurno(false).build();
        com.grupo73ISII.api_sistemaTurnos.model.EstadoFacturacion estadoDevuelto =
            com.grupo73ISII.api_sistemaTurnos.model.EstadoFacturacion.builder()
                .idEstadoFacturacion(3L).build();

        when(facturacionRepository.findById(5L)).thenReturn(Optional.of(facturacion));
        when(turnoService.findByFacturaId(5L)).thenReturn(Optional.of(turno));
        when(turnoService.turnoCancelado(1L)).thenReturn(true);
        when(estadoFacturacionService.findById(3L)).thenReturn(Optional.of(estadoDevuelto));

        assertDoesNotThrow(() -> facturacionService.procesarDevolucion(5L));

        verify(pagoService).generarDevolucion(10L);
    }

    // CASO 2: Pago fuera del período permitido
    @Test
    void procesarDevolucion_fueraDelPeriodo_lanzaExcepcion() {
        Facturacion facturacion = buildFacturacion(5L, 2L, 10L);
        Turno turno = Turno.builder().idTurno(1L).estadoTurno(false).build();

        when(facturacionRepository.findById(5L)).thenReturn(Optional.of(facturacion));
        when(turnoService.findByFacturaId(5L)).thenReturn(Optional.of(turno));
        when(turnoService.turnoCancelado(1L)).thenReturn(true);
        doThrow(new RuntimeException("El período para solicitar la devolución ha expirado."))
            .when(pagoService).generarDevolucion(10L);

        assertThrows(RuntimeException.class, () -> facturacionService.procesarDevolucion(5L));
    }

    // CASO 3: Turno activo (no cancelado)
    @Test
    void procesarDevolucion_turnoActivo_lanzaExcepcion() {
        Facturacion facturacion = buildFacturacion(5L, 2L, 10L);
        Turno turno = Turno.builder().idTurno(1L).estadoTurno(true).build();

        when(facturacionRepository.findById(5L)).thenReturn(Optional.of(facturacion));
        when(turnoService.findByFacturaId(5L)).thenReturn(Optional.of(turno));
        when(turnoService.turnoCancelado(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> facturacionService.procesarDevolucion(5L));
    }

    // CASO 4: Turno no abonado
    @Test
    void procesarDevolucion_turnoNoPagado_lanzaExcepcion() {
        Facturacion facturacion = buildFacturacion(5L, 1L, 10L); // estado 1 = pendiente

        when(facturacionRepository.findById(5L)).thenReturn(Optional.of(facturacion));

        assertThrows(RuntimeException.class, () -> facturacionService.procesarDevolucion(5L));
    }

    // CASO 5: Pasarela rechaza la devolución
    @Test
    void procesarDevolucion_pasarelaRechaza_lanzaExcepcion() {
        Facturacion facturacion = buildFacturacion(5L, 2L, 10L);
        Turno turno = Turno.builder().idTurno(1L).estadoTurno(false).build();

        when(facturacionRepository.findById(5L)).thenReturn(Optional.of(facturacion));
        when(turnoService.findByFacturaId(5L)).thenReturn(Optional.of(turno));
        when(turnoService.turnoCancelado(1L)).thenReturn(true);
        doThrow(new RuntimeException("Error en la pasarela de pago"))
            .when(pagoService).generarDevolucion(10L);

        assertThrows(RuntimeException.class, () -> facturacionService.procesarDevolucion(5L));
    }

    // CASO 6: Factura no encontrada (id 999)
    @Test
    void procesarDevolucion_facturaNoExiste_lanzaExcepcion() {
        when(facturacionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> facturacionService.procesarDevolucion(999L));
    }

    // CASO 7: idPago nulo
    @Test
    void procesarDevolucion_idPagoNulo_lanzaExcepcion() {
        Facturacion facturacion = buildFacturacion(5L, 2L, null);
        Turno turno = Turno.builder().idTurno(1L).build();

        when(facturacionRepository.findById(5L)).thenReturn(Optional.of(facturacion));
        when(turnoService.findByFacturaId(5L)).thenReturn(Optional.of(turno));
        when(turnoService.turnoCancelado(1L)).thenReturn(true);
        doThrow(new RuntimeException("El id del pago no puede ser nulo."))
            .when(pagoService).generarDevolucion(null);

        assertThrows(RuntimeException.class, () -> facturacionService.procesarDevolucion(5L));
    }

    // CASO 8: idFactura nulo
    @Test
    void procesarDevolucion_idFacturaNulo_lanzaExcepcion() {
        assertThrows(RuntimeException.class, () -> facturacionService.procesarDevolucion(null));
    }
}
