package com.grupo73ISII.api_sistemaTurnos.service.impl;

import com.grupo73ISII.api_sistemaTurnos.model.DetallePago;
import com.grupo73ISII.api_sistemaTurnos.model.EstadoFacturacion;
import com.grupo73ISII.api_sistemaTurnos.model.Facturacion;
import com.grupo73ISII.api_sistemaTurnos.model.MetodoDePago;
import com.grupo73ISII.api_sistemaTurnos.repository.FacturacionRepository;
import com.grupo73ISII.api_sistemaTurnos.service.IDetallePagoService;
import com.grupo73ISII.api_sistemaTurnos.service.IEstadoFacturacionService;
import com.grupo73ISII.api_sistemaTurnos.service.IFacturacionService;
import com.grupo73ISII.api_sistemaTurnos.service.IMetodoDePagoService;
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
    private IDetallePagoService detallePagoService;
    @Autowired
    private IEstadoFacturacionService estadoFacturacionService;

    @Override
    @Transactional
    public Facturacion inicializarPago(Long idMetodoPago, BigDecimal valorMonetario) {
        MetodoDePago metodoDePago = metodoDePagoService.findById(idMetodoPago)
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        // El número de transacción se deja nulo inicialmente
        DetallePago detallePago = DetallePago.builder()
                .metodoDePago(metodoDePago)
                .build();
        detallePagoService.save(detallePago);

        // Se asume que el ID 1 corresponde al estado 'Pendiente' o 'Iniciado'
        EstadoFacturacion estado = estadoFacturacionService.findById(1L)
                .orElseThrow(() -> new RuntimeException("Estado de facturación inicial no encontrado"));

        Facturacion facturacion = Facturacion.builder()
                .fechaRegistro(LocalDateTime.now())
                .estadoFacturacion(estado)
                .detallePago(detallePago)
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
