package com.grupo73ISII.api_sistemaTurnos.service.impl;

import com.grupo73ISII.api_sistemaTurnos.dto.DisponibilidadTurnoDTO;
import com.grupo73ISII.api_sistemaTurnos.dto.TurnoRequestDTO;
import com.grupo73ISII.api_sistemaTurnos.dto.TurnoResponseDTO;
import com.grupo73ISII.api_sistemaTurnos.dto.TurnoUpdateDTO;
import com.grupo73ISII.api_sistemaTurnos.model.Cliente;
import com.grupo73ISII.api_sistemaTurnos.model.FranjaHoraria;
import com.grupo73ISII.api_sistemaTurnos.model.Reserva;
import com.grupo73ISII.api_sistemaTurnos.model.Turno;
import com.grupo73ISII.api_sistemaTurnos.repository.TurnoRepository;
import com.grupo73ISII.api_sistemaTurnos.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TurnoServiceImpl implements ITurnoService {

    @Autowired
    private TurnoRepository turnoRepository;
    @Autowired
    private IClienteService clienteService;
    @Autowired
    private IReservaService reservaService;
    @Autowired
    private IFacturacionService facturacionService;
    @Autowired
    private IFranjaHorariaService franjaHorariaService;

    @Override
    @Transactional
    public TurnoResponseDTO procesarTurno(TurnoRequestDTO turnoRequestDTO) {

        // 2.S Verifica que los datos del cliente son validos
        Cliente cliente = verificarCliente(turnoRequestDTO.getIdCliente());


        // 3.S: Busca disponibilidad
        if (verificarDisponibilidad(turnoRequestDTO.getReservaRequest().getIdFranjaHoraria(), turnoRequestDTO.getFechaTurno())) {
            throw new RuntimeException("El turno no está disponible en esa fecha y horario.");
        }
        // 4.S: Genera Reserva
        Reserva reservaGenerada = reservaService.procesarReserva(turnoRequestDTO.getReservaRequest());

        // 5.S Registra Turno
        Turno turnoGenerado = registrarTurno(turnoRequestDTO.getFechaTurno(), reservaGenerada, cliente);

        TurnoResponseDTO response = new TurnoResponseDTO();
        response.setIdTurno(turnoGenerado.getIdTurno());
        return response;
    }

    @Override
    @Transactional
    public TurnoResponseDTO editarTurno(Long idTurno, TurnoUpdateDTO turnoUpdateDTO) {
        Turno turno = turnoRepository.findById(idTurno)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        // 2.S Verifica que los datos del cliente son validos
        verificarCliente(turno.getCliente().getIdCliente());


        // 3.S: Busca disponibilidad
        if (verificarDisponibilidad(turnoUpdateDTO.getNuevoIdFranjaHoraria(), turnoUpdateDTO.getNuevaFechaTurno())) {
            throw new RuntimeException("La nueva fecha y horario para el turno no están disponibles.");
        }

        // 4.S: Verifica que la reserva está paga
        if (!facturacionService.estaPagada(turno.getReserva().getFacturacion().getIdFacturacion())) {
            throw new RuntimeException("No se puede modificar un turno que no está pagado.");
        }


        //S: Modifica el turno
        reservaService.modificarReserva(turno.getReserva(), turnoUpdateDTO.getNuevoIdFranjaHoraria(), turnoUpdateDTO.getNuevoIdCancha());

        //S: Modifica el turno
        Turno turnoModificado = modificarTurno(idTurno, turnoUpdateDTO.getNuevaFechaTurno());

        TurnoResponseDTO response = new TurnoResponseDTO();
        response.setIdTurno(turnoModificado.getIdTurno());
        return response;
    }

    @Override
    @Transactional
    public void cancelarTurno(Long idTurno) {
        Turno turno = turnoRepository.findById(idTurno)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        turno.setEstadoTurno(false);
        turno.setFechaCancelacion(LocalDate.now());
        turnoRepository.save(turno);
    }

    @Override
    public Turno registrarTurno(LocalDate fechaTurno, Reserva reserva, Cliente cliente) {
        Turno nuevoTurno = Turno.builder()
                .fechaTurno(fechaTurno)
                .estadoTurno(true)
                .reserva(reserva)
                .cliente(cliente)
                .build();
        return turnoRepository.save(nuevoTurno);
    }

    @Override
    public Turno modificarTurno(Long idTurno, LocalDate nuevaFecha) {
        Turno turno = turnoRepository.findById(idTurno)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado al intentar modificar."));
        turno.setFechaTurno(nuevaFecha);
        return turnoRepository.save(turno);
    }

    @Override
    public boolean verificarDisponibilidad(String idFranjaHoraria, LocalDate fechaDeTurno) {
        return turnoRepository.findTurnoByFranjaAndFecha(idFranjaHoraria, fechaDeTurno).isPresent();
    }


    // Para el front
    @Override
    @Transactional(readOnly = true)
    public List<DisponibilidadTurnoDTO> consultarDisponibilidad(LocalDate fecha, Long idCancha) {
        List<FranjaHoraria> todasLasFranjas = franjaHorariaService.findAll();
        List<Turno> turnosOcupados = turnoRepository.findAllByFechaTurnoAndReserva_Cancha_Id(fecha, idCancha);
        Set<String> idsFranjasOcupadas = turnosOcupados.stream()
                .map(turno -> turno.getReserva().getFranjaHoraria().getId_franjaHoraria())
                .collect(Collectors.toSet());

        return todasLasFranjas.stream()
                .map(franja -> new DisponibilidadTurnoDTO(
                        franja.getId_franjaHoraria(),
                        franja.getHoraInicio(),
                        franja.getHoraFin(),
                        idsFranjasOcupadas.contains(franja.getId_franjaHoraria()) ? "ocupada" : "libre"
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Turno> listarTurnosActivos() {
        return turnoRepository.findAllByEstadoTurno(true);
    }

    private Cliente verificarCliente(Long idCliente) {
        if (!clienteService.validarDatos(idCliente)) {
            throw new RuntimeException("El cliente se encuentra suspendido.");
        }
        return clienteService.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }
}
