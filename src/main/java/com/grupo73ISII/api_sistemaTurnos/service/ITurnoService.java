package com.grupo73ISII.api_sistemaTurnos.service;

import com.grupo73ISII.api_sistemaTurnos.dto.DisponibilidadTurnoDTO;
import com.grupo73ISII.api_sistemaTurnos.dto.TurnoRequestDTO;
import com.grupo73ISII.api_sistemaTurnos.dto.TurnoResponseDTO;
import com.grupo73ISII.api_sistemaTurnos.dto.TurnoUpdateDTO;
import com.grupo73ISII.api_sistemaTurnos.model.Cliente;
import com.grupo73ISII.api_sistemaTurnos.model.Reserva;
import com.grupo73ISII.api_sistemaTurnos.model.Turno;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ITurnoService {
    TurnoResponseDTO procesarTurno(TurnoRequestDTO turnoRequestDTO);
    Turno registrarTurno(LocalDate fechaTurno, Reserva reserva, Cliente cliente);
    boolean consultarDisponibilidad(String idFranjaHoraria, LocalDate fechaDeTurno, Long idCancha);
    TurnoResponseDTO editarTurno(Long idTurno, TurnoUpdateDTO turnoUpdateDTO);
    Turno modificarTurno(Long idTurno, LocalDate nuevaFecha);
    void cancelarTurno(Long idTurno);
    boolean turnoCancelado(Long idTurno);
    Optional<Turno> findByFacturaId(Long idFactura);
    List<DisponibilidadTurnoDTO> consultarDisponibilidad(LocalDate fecha, Long idCancha);
    List<Turno> listarTurnosActivos();
    List<TurnoResponseDTO> listarTodos();
}
