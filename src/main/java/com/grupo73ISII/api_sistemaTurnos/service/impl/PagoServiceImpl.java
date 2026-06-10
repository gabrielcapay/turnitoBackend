package com.grupo73ISII.api_sistemaTurnos.service.impl;

import com.grupo73ISII.api_sistemaTurnos.model.MetodoDePago;
import com.grupo73ISII.api_sistemaTurnos.model.Pago;
import com.grupo73ISII.api_sistemaTurnos.repository.FacturacionRepository;
import com.grupo73ISII.api_sistemaTurnos.repository.PagoRepository;
import com.grupo73ISII.api_sistemaTurnos.service.IMetodoDePago;
import com.grupo73ISII.api_sistemaTurnos.service.IMetodoDePagoService;
import com.grupo73ISII.api_sistemaTurnos.service.IPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class PagoServiceImpl implements IPagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private FacturacionRepository facturacionRepository;

    @Autowired
    private IMetodoDePagoService metodoDePagoService;

    @Autowired
    private Map<String, IMetodoDePago> metodosDePago;

    @Override
    public Pago save(Pago pago) {
        return pagoRepository.save(pago);
    }

    @Override
    public Optional<Pago> findById(Long id) {
        return pagoRepository.findById(id);
    }

    //-------------------------------Caso de uso Cambiar Método de Pago-------------------------
    @Override
    @Transactional
    public Pago actualizarMetodoPago(Long idPago, Long idMetodoNuevo) {
        Pago pago = findById(idPago)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + idPago));

        MetodoDePago nuevoMetodo = metodoDePagoService.findById(idMetodoNuevo)
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado: " + idMetodoNuevo));

        pago.setMetodoDePago(nuevoMetodo);
        return save(pago);
    }


    //-------------------------------Caso de uso Registrar Pago-----------------------------------
    @Override
    @Transactional
    public void procesarPago(String token, String paymentMethodId, String email, Long idPago) {
        validarDatosPago(token, paymentMethodId, email, idPago);

        Pago pago = findById(idPago)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + idPago));
        
        if (pago.isEstado()) {
            throw new RuntimeException("La factura ya fue pagada.");
        }

        Long idFacturacion = facturacionRepository.findByPagoIdPago(idPago)
                .map(f -> f.getIdFacturacion())
                .orElseThrow(() -> new RuntimeException("Facturación no encontrada para el pago: " + idPago));

        String nombreEstrategia = pago.getMetodoDePago().getDescripcion().toLowerCase().replaceAll("\\s+", "");
        IMetodoDePago estrategia = metodosDePago.get(nombreEstrategia);
        if (estrategia == null) {
            throw new RuntimeException("No se encontró una implementación de pago para: " + nombreEstrategia);
        }

        String numeroTransaccion = estrategia.ejecutarPago(pago.getMonto(), idFacturacion, token, email, paymentMethodId);
        
        registrarElPago(idPago, numeroTransaccion);
    }

    @Override
    @Transactional
    public Pago registrarElPago(Long idPago, String numeroTransaccion) {
        Pago pago = findById(idPago)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado al intentar registrarlo: " + idPago));
        pago.setNumeroTransaccion(numeroTransaccion);
        pago.setEstado(true); // Marcar como pagado
        pago.setFechaPago(LocalDateTime.now());
        return save(pago);
    }
    //-------------------------------Caso de uso solicitar devolucion-----------------------------------
    @Override
    @Transactional
    public void generarDevolucion(Long idPago) {
        if (idPago == null) {
            throw new RuntimeException("El id del pago no puede ser nulo.");
        }
        // 5. valida que el pago esté dentro del período permitido para devolución.
        validarPeriodoDevolucion(idPago);


        Pago pago = findById(idPago)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + idPago));

        String nombreEstrategia = pago.getMetodoDePago().getDescripcion().toLowerCase().replaceAll("\\s+", "");
        IMetodoDePago estrategia = metodosDePago.get(nombreEstrategia);
        if (estrategia == null) {
            throw new RuntimeException("No se encontró una implementación de pago para: " + nombreEstrategia);
        }

        // 6. procesa la devolución a través del método de pago.
        estrategia.ejecutarDevolucion(pago.getNumeroTransaccion());

        // 5. registra la devolución con la referencia recibida
        registrarDevolucion(idPago);
    }

    @Override
    @Transactional
    public Pago registrarDevolucion(Long idPago) {
        Pago pago = findById(idPago)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado al intentar registrar la devolución: " + idPago));
        
        pago.setEstado(false); // Marcar como no pagado/devuelto
        pago.setFechaDevolucion(LocalDateTime.now());
        return save(pago);
    }

    private void validarDatosPago(String token, String paymentMethodId, String email, Long idPago) {
        if (idPago == null) {
            throw new RuntimeException("El id del pago no puede ser nulo.");
        }
        if (token == null || token.isBlank()) {
            throw new RuntimeException("El token de transacción no puede ser nulo o vacío.");
        }
        if (paymentMethodId == null) {
            throw new RuntimeException("El método de pago no puede ser nulo.");
        }
        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new RuntimeException("El email es inválido.");
        }
    }

    @Override
    public void validarPeriodoDevolucion(Long idPago) {
        Pago pago = findById(idPago)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + idPago));

        if (pago.getFechaPago() == null) {
            throw new RuntimeException("El pago no tiene una fecha de realización, no se puede procesar la devolución.");
        }

        long diasDesdeElPago = Duration.between(pago.getFechaPago(), LocalDateTime.now()).toDays();

        if (diasDesdeElPago > 7) {
            throw new RuntimeException("El período para solicitar la devolución ha expirado. Límite: 7 días.");
        }
    }
}
