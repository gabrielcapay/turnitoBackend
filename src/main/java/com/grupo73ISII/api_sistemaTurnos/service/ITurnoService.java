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

public interface ITurnoService {
    TurnoResponseDTO procesarTurno(TurnoRequestDTO turnoRequestDTO);
    Turno registrarTurno(LocalDate fechaTurno, Reserva reserva, Cliente cliente);
    boolean verificarDisponibilidad(String idFranjaHoraria, LocalDate fechaDeTurno);
    TurnoResponseDTO editarTurno(Long idTurno, TurnoUpdateDTO turnoUpdateDTO);
    Turno modificarTurno(Long idTurno, LocalDate nuevaFecha);
    void cancelarTurno(Long idTurno);
    List<DisponibilidadTurnoDTO> consultarDisponibilidad(LocalDate fecha, Long idCancha);
    List<Turno> listarTurnosActivos();
}
