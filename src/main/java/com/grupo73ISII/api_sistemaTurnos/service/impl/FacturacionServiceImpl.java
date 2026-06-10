package com.grupo73ISII.api_sistemaTurnos.service.impl;

import com.grupo73ISII.api_sistemaTurnos.dto.ProcesarPagoRequestDTO;
import com.grupo73ISII.api_sistemaTurnos.model.*;
import com.grupo73ISII.api_sistemaTurnos.repository.FacturacionRepository;
import com.grupo73ISII.api_sistemaTurnos.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class FacturacionServiceImpl implements IFacturacionService {

    @Autowired
    private FacturacionRepository facturacionRepository;
    @Autowired
    private IMetodoDePagoService metodoDePagoService;
    @Autowired
    private IPagoService pagoService;
    @Autowired
    private IEstadoFacturacionService estadoFacturacionService;
    @Autowired
    @Lazy
    private ITurnoService turnoService;

    /**
     * Crea y persiste las entidades Pago y Facturacion iniciales para una nueva reserva.
     * El pago se crea en estado 'no pagado' y la facturación en estado 'pendiente'.
     *
     * @param idMetodoPago El ID del método de pago seleccionado para esta transacción.
     * @param valorMonetario El monto total a pagar.
     * @return La entidad Facturacion recién creada y guardada.
     */
    @Override
    @Transactional
    public Facturacion inicializarPago(Long idMetodoPago, BigDecimal valorMonetario) {
        // ... (código existente sin cambios)
        MetodoDePago metodoDePago = metodoDePagoService.findById(idMetodoPago)
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        Pago nuevoPago = Pago.builder()
                .metodoDePago(metodoDePago)
                .monto(valorMonetario.doubleValue())
                .estado(false) 
                .build();
        pagoService.save(nuevoPago);

        EstadoFacturacion estado = estadoFacturacionService.findById(1L) 
                .orElseThrow(() -> new RuntimeException("Estado de facturación inicial no encontrado"));

        Facturacion facturacion = Facturacion.builder()
                .fechaRegistro(LocalDateTime.now())
                .estadoFacturacion(estado)
                .pago(nuevoPago)
                .build();

        return facturacionRepository.save(facturacion);
    }

    @Override
    public Optional<Facturacion> findById(Long id) {
        return facturacionRepository.findById(id);
    }

    /**
     * Procesa el pago de una factura que se encuentra en estado pendiente.
     * Este método orquesta la comunicación con el servicio de pago y actualiza el estado de la factura a "Pagado".
     *
     * @param request El DTO que contiene los datos de la transacción de pago, incluyendo el ID de la factura.
     */
    @Override
    @Transactional
    public void procesarFacturaPendiente(ProcesarPagoRequestDTO request) {
        // ... (código existente sin cambios)
        Facturacion facturacion = findById(request.getIdFactura())
                .orElseThrow(() -> new RuntimeException("Facturación no encontrada con ID: " + request.getIdFactura()));

        Long idPago = facturacion.getPago().getIdPago();

        // 3.  Envía los datos para procesar el cobro.
        pagoService.procesarPago(request.getToken(), request.getPaymentMethodId(), request.getEmail(), idPago);

        // 5. actualiza el estado a Pagado
        actualizarFacturaAPagado(request.getIdFactura());
    }

    @Override
    @Transactional
    public void actualizarFacturaAPagado(Long idFactura) {
        // ... (código existente sin cambios)
        Facturacion facturacion = findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Facturación no encontrada con ID: " + idFactura));

        EstadoFacturacion estadoPagado = estadoFacturacionService.findById(2L) // Asumiendo 2L = Pagado
                .orElseThrow(() -> new RuntimeException("Estado de facturación 'Pagado' no encontrado"));
        
        facturacion.setEstadoFacturacion(estadoPagado);
        facturacionRepository.save(facturacion);
    }
    
    //-------------------------------Caso de uso Cambiar Método de Pago-------------------------
    @Override
    @Transactional
    public void procesarCambiodeMetodo(Long idFactura, Long idMetodoPago) {
        // 2. Verifica que la factura NO esté paga (debe estar pendiente)
        if (estaPagada(idFactura)) {
            throw new RuntimeException("No hay factura pendiente: la factura ya fue pagada.");
        }

        Facturacion facturacion = findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Facturación no encontrada con ID: " + idFactura));

        // 3. Actualiza el método de pago en el Pago asociado
        pagoService.actualizarMetodoPago(facturacion.getPago().getIdPago(), idMetodoPago);
    }

    //-------------------------------Caso de uso solicitar devolucion---------------------------
    @Override
    @Transactional
    public void procesarDevolucion(Long idFactura) {
        if (idFactura == null) {
            throw new RuntimeException("El id de la factura no puede ser nulo.");
        }

        // 1.  verifica que la factura esté en "Devolución Pendiente" (estado al que pasa al cancelar el turno).
        if (!estaEnDevolucionPendiente(idFactura)) {
            throw new RuntimeException("La factura no está pendiente de devolución.");
        }

        Turno turno = turnoService.findByFacturaId(idFactura)
                .orElseThrow(() -> new RuntimeException("No se encontró un turno asociado a la factura con ID: " + idFactura));

        // 3. Verificar que el turno esté cancelado
        if (!turnoService.turnoCancelado(turno.getIdTurno())) {
            throw new RuntimeException("No se puede procesar la devolución de un turno que no está cancelado.");
        }

        Facturacion facturacion = findById(idFactura).orElseThrow(() -> new RuntimeException("Facturación no encontrada con ID: " + idFactura));
        
        // 5. realiza la solicitud de devolución sobre la factura correspondiente
        pagoService.generarDevolucion(facturacion.getPago().getIdPago());

        // 6. Actualizar el estado de la facturación a "Devuelto"
        actualizarFacturaADevuelto(idFactura);
    }

    @Override
    @Transactional
    public void actualizarFacturaADevolucionPendiente(Long idFactura) {
        Facturacion facturacion = findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Facturación no encontrada con ID: " + idFactura));

        EstadoFacturacion estadoDevolucionPendiente = estadoFacturacionService.findById(4L) // Asumiendo 4L = Devolución Pendiente
                .orElseThrow(() -> new RuntimeException("Estado 'Devolución Pendiente' no encontrado"));

        facturacion.setEstadoFacturacion(estadoDevolucionPendiente);
        facturacionRepository.save(facturacion);
    }

    @Override
    @Transactional
    public void actualizarFacturaADevuelto(Long idFactura) {
        Facturacion facturacion = findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Facturación no encontrada con ID: " + idFactura));

        EstadoFacturacion estadoDevuelto = estadoFacturacionService.findById(3L) // Asumiendo 3L = Devuelto
                .orElseThrow(() -> new RuntimeException("Estado de facturación 'Devuelto' no encontrado"));
        
        facturacion.setEstadoFacturacion(estadoDevuelto);
        facturacionRepository.save(facturacion);
    }

    //----------------------------------------------------------------------------------------
    @Override
    public boolean estaPagada(Long idFacturacion) {
        return findById(idFacturacion)
                .map(facturacion -> facturacion.getEstadoFacturacion().getIdEstadoFacturacion().equals(2L)) // Asumiendo 2L = Pagado
                .orElse(false);
    }

    private boolean estaEnDevolucionPendiente(Long idFacturacion) {
        return findById(idFacturacion)
                .map(facturacion -> facturacion.getEstadoFacturacion().getIdEstadoFacturacion().equals(4L)) // 4L = Devolución Pendiente
                .orElse(false);
    }
}
