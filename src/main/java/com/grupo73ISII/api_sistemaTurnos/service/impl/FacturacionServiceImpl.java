package com.grupo73ISII.api_sistemaTurnos.service.impl;

import com.grupo73ISII.api_sistemaTurnos.model.EstadoFacturacion;
import com.grupo73ISII.api_sistemaTurnos.model.Facturacion;
import com.grupo73ISII.api_sistemaTurnos.model.MetodoDePago;
import com.grupo73ISII.api_sistemaTurnos.model.Pago;
import com.grupo73ISII.api_sistemaTurnos.repository.FacturacionRepository;
import com.grupo73ISII.api_sistemaTurnos.service.IEstadoFacturacionService;
import com.grupo73ISII.api_sistemaTurnos.service.IFacturacionService;
import com.grupo73ISII.api_sistemaTurnos.service.IMetodoDePagoService;
import com.grupo73ISII.api_sistemaTurnos.service.IPagoService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    @Transactional
    public Facturacion inicializarPago(Long idMetodoPago, BigDecimal valorMonetario) {
        MetodoDePago metodoDePago = metodoDePagoService.findById(idMetodoPago)
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        Pago nuevoPago = Pago.builder()
                .metodoDePago(metodoDePago)
                .monto(valorMonetario.doubleValue())
                .estado(false) // Inicia como no pagado
                .build();
        pagoService.save(nuevoPago);

        EstadoFacturacion estado = estadoFacturacionService.findById(1L) // Asumiendo 1L = Pendiente
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

    @Override
    public boolean estaPagada(Long idFacturacion) {
        return findById(idFacturacion)
                .map(facturacion -> facturacion.getEstadoFacturacion().getIdEstadoFacturacion().equals(2L)) // Asumiendo 2L = Pagado
                .orElse(false);
    }
}
