package com.grupo73ISII.api_sistemaTurnos.service.impl;

import com.grupo73ISII.api_sistemaTurnos.dto.ReservaRequestDTO;
import com.grupo73ISII.api_sistemaTurnos.model.Cancha;
import com.grupo73ISII.api_sistemaTurnos.model.Facturacion;
import com.grupo73ISII.api_sistemaTurnos.model.FranjaHoraria;
import com.grupo73ISII.api_sistemaTurnos.model.Reserva;
import com.grupo73ISII.api_sistemaTurnos.repository.ReservaRepository;
import com.grupo73ISII.api_sistemaTurnos.service.ICanchaService;
import com.grupo73ISII.api_sistemaTurnos.service.IFacturacionService;
import com.grupo73ISII.api_sistemaTurnos.service.IFranjaHorariaService;
import com.grupo73ISII.api_sistemaTurnos.service.IReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ReservaServiceImpl implements IReservaService {

    @Autowired
    private ICanchaService canchaService;
    @Autowired
    private IFacturacionService facturacionService;
    @Autowired
    private IFranjaHorariaService franjaHorariaService;
    @Autowired
    private ReservaRepository reservaRepository;


    @Override
    @Transactional
    public Reserva procesarReserva(ReservaRequestDTO reservaRequestDTO) {
        // 4.1 S Consulta el precio de la cancha
        BigDecimal precio = canchaService.obtenerPrecio(reservaRequestDTO.getIdCancha());

        // 4.2 S Inicializa metodo de Pago
        Facturacion facturacion = facturacionService.inicializarPago(reservaRequestDTO.getIdMetodoDePago(), precio);


        return registrarReserva(precio, facturacion.getIdFacturacion(), reservaRequestDTO.getIdCancha(), reservaRequestDTO.getIdFranjaHoraria());
    }

    @Override
    public Reserva modificarReserva(Reserva reserva, String nuevoIdFranjaHoraria, Long nuevoIdCancha) {
        FranjaHoraria nuevaFranja = franjaHorariaService.findById(nuevoIdFranjaHoraria)
                .orElseThrow(() -> new RuntimeException("La nueva franja horaria no existe."));
        Cancha nuevaCancha = canchaService.findById(nuevoIdCancha)
                .orElseThrow(() -> new RuntimeException("La nueva cancha no existe."));

        reserva.setFranjaHoraria(nuevaFranja);
        reserva.setCancha(nuevaCancha);
        return reservaRepository.save(reserva);
    }



    @Override
    public Reserva registrarReserva(BigDecimal monto, Long idFacturacion, Long idCancha, String idFranjaHoraria) {

        Facturacion facturacion = facturacionService.findById(idFacturacion)
                .orElseThrow(() -> new RuntimeException("Facturación no encontrada"));
        Cancha cancha = canchaService.findById(idCancha)
                .orElseThrow(() -> new RuntimeException("Cancha no encontrada"));
        FranjaHoraria franjaHoraria = franjaHorariaService.findById(idFranjaHoraria)
                .orElseThrow(() -> new RuntimeException("Franja horaria no encontrada"));


        Reserva nuevaReserva = Reserva.builder()
                .montoAPagar(monto)
                .facturacion(facturacion)
                .cancha(cancha)
                .franjaHoraria(franjaHoraria)
                .build();
        
        // 3. Guardar y devolver
        return reservaRepository.save(nuevaReserva);
    }


}
