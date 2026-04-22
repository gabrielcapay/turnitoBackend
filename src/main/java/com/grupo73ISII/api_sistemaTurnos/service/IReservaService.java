package com.grupo73ISII.api_sistemaTurnos.service;

import com.grupo73ISII.api_sistemaTurnos.dto.ReservaRequestDTO;
import com.grupo73ISII.api_sistemaTurnos.model.Reserva;
import java.math.BigDecimal;

public interface IReservaService {
    Reserva procesarReserva(ReservaRequestDTO reservaRequestDTO);
    Reserva registrarReserva(BigDecimal monto, Long idFacturacion, Long idCancha, String idFranjaHoraria);
    Reserva modificarReserva(Reserva reserva, String nuevoIdFranjaHoraria, Long nuevoIdCancha);
}
