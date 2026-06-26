package com.grupo73ISII.api_sistemaTurnos.service.impl;

import com.grupo73ISII.api_sistemaTurnos.dto.*;
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
import java.util.Optional;
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

    // ... (otros métodos sin cambios)

    @Override
    @Transactional(readOnly = true)
    public List<TurnoResponseDTO> listarTodos() {
        List<Turno> turnos = turnoRepository.findAll();
        return turnos.stream()
                .map(this::convertirA_DTO)
                .collect(Collectors.toList());
    }

    private TurnoResponseDTO convertirA_DTO(Turno turno) {
        ClienteSimplificadoDTO clienteDTO = new ClienteSimplificadoDTO(
                turno.getCliente().getIdCliente(),
                turno.getCliente().getPersona().getNombre(),
                turno.getCliente().getPersona().getApellido()
        );

        return new TurnoResponseDTO(
                turno.getIdTurno(),
                turno.getFechaTurno(),
                turno.getEstadoTurno(),
                clienteDTO,
                turno.getReserva()
        );
    }
    
    // ... (resto de los métodos sin cambios)
    @Override
    @Transactional
    public TurnoResponseDTO procesarTurno(TurnoRequestDTO turnoRequestDTO) {

        validarDatos(turnoRequestDTO);

        // 2.S Verifica que los datos del cliente son validos
        Cliente cliente = verificarCliente(turnoRequestDTO.getIdCliente());


        // 3.S: Busca disponibilidad
        if (consultarDisponibilidad(
                turnoRequestDTO.getReservaRequest().getIdFranjaHoraria(),
                turnoRequestDTO.getFechaTurno(),
                turnoRequestDTO.getReservaRequest().getIdCancha()
        )) {
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
        validarDatosActualizacion(idTurno, turnoUpdateDTO);

        Turno turno = turnoRepository.findById(idTurno)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        if (!turno.getEstadoTurno()) {
            throw new RuntimeException("No se puede modificar un turno cancelado.");
        }

        // 2.S Verifica que los datos del cliente son validos
        verificarCliente(turno.getCliente().getIdCliente());


        // 3.S: Busca disponibilidad
        if (consultarDisponibilidad(turnoUpdateDTO.getNuevoIdFranjaHoraria(), turnoUpdateDTO.getNuevaFechaTurno(), turnoUpdateDTO.getNuevoIdCancha())) {
            throw new RuntimeException("La nueva fecha y horario para el turno no están disponibles.");
        }

        // 4.S: Verifica que la reserva este paga
        if (!facturacionService.estaPagada(turno.getReserva().getFacturacion().getIdFacturacion())) {
            throw new RuntimeException("No se puede modificar un turno que no está pagado.");
        }


        //S: Modifica el turno
        reservaService.modificarReserva(turno.getReserva(), turnoUpdateDTO.getNuevoIdFranjaHoraria(), turnoUpdateDTO.getNuevoIdCancha());
        Turno turnoModificado = modificarTurno(idTurno, turnoUpdateDTO.getNuevaFechaTurno());

        TurnoResponseDTO response = new TurnoResponseDTO();
        response.setIdTurno(turnoModificado.getIdTurno());
        return response;
    }

    @Override
    @Transactional
    public void cancelarTurno(Long idTurno) {
        // 2. Verifica que el turno exista y esté activo
        Turno turno = turnoRepository.findById(idTurno)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        if (!turno.getEstadoTurno()) {
            throw new RuntimeException("El turno ya está cancelado.");
        }

        // 3. La reserva verifica si requiere devolución
        reservaService.verificarDevolucionReserva(turno.getReserva().getIdReserva());

        // 6. Confirma la baja del turno
        confirmarCancelacion(idTurno);
    }

    private void confirmarCancelacion(Long idTurno) {
        Turno turno = turnoRepository.findById(idTurno)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado al confirmar cancelación."));
        turno.setEstadoTurno(false);
        turno.setFechaCancelacion(LocalDate.now());
        turnoRepository.save(turno);
    }

    @Override
    public boolean turnoCancelado(Long idTurno) {
        return turnoRepository.findById(idTurno)
                .map(turno -> !turno.getEstadoTurno()) // Devuelve true si estadoTurno es false
                .orElse(true); // Si no se encuentra el turno, se considera como no activo/cancelado
    }

    @Override
    public Optional<Turno> findByFacturaId(Long idFactura) {
        return turnoRepository.findByReserva_Facturacion_IdFacturacion(idFactura);
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
    public boolean consultarDisponibilidad(String idFranjaHoraria, LocalDate fechaDeTurno, Long idCancha) {
        return turnoRepository.existeTurnoActivo(idFranjaHoraria, fechaDeTurno, idCancha);
    }


    // Para el front
    @Override
    @Transactional(readOnly = true)
    public List<DisponibilidadTurnoDTO> consultarDisponibilidad(LocalDate fecha, Long idCancha) {
        List<FranjaHoraria> todasLasFranjas = franjaHorariaService.findAll();
        
        // Se utiliza el nuevo método que filtra por estado del turno
        List<Turno> turnosOcupados = turnoRepository.findActiveTurnosByFechaAndCancha(fecha, idCancha);

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

    private void validarDatosActualizacion(Long idTurno, TurnoUpdateDTO dto) {
        if (idTurno == null) {
            throw new RuntimeException("El id del turno no puede ser nulo.");
        }
        if (dto.getNuevaFechaTurno() == null) {
            throw new RuntimeException("La nueva fecha del turno no puede ser nula.");
        }
        if (dto.getNuevoIdCancha() == null) {
            throw new RuntimeException("El id de la cancha no puede ser nulo.");
        }
        if (dto.getNuevoIdFranjaHoraria() == null) {
            throw new RuntimeException("El id de la franja horaria no puede ser nulo.");
        }
        if (dto.getNuevaFechaTurno().isBefore(LocalDate.now())) {
            throw new RuntimeException("La nueva fecha del turno no puede ser anterior a la fecha actual.");
        }
    }

    private void validarDatos(TurnoRequestDTO dto) {
        if (dto.getIdCliente() == null) {
            throw new RuntimeException("El id del cliente no puede ser nulo.");
        }
        if (dto.getReservaRequest() == null) {
            throw new RuntimeException("Los datos de la reserva no pueden ser nulos.");
        }
        if (dto.getFechaTurno() == null) {
            throw new RuntimeException("La fecha del turno no puede ser nula.");
        }
        if (dto.getFechaTurno().isBefore(LocalDate.now())) {
            throw new RuntimeException("La fecha del turno no puede ser anterior a la fecha actual.");
        }
    }

    //Esto va en CLientes.
    private Cliente verificarCliente(Long idCliente) {
        if (!clienteService.validarDatos(idCliente)) {
            throw new RuntimeException("El cliente se encuentra suspendido.");
        }
        return clienteService.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }
}
