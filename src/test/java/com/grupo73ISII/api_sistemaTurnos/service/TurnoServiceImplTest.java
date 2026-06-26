package com.grupo73ISII.api_sistemaTurnos.service;

import com.grupo73ISII.api_sistemaTurnos.dto.ReservaRequestDTO;
import com.grupo73ISII.api_sistemaTurnos.dto.TurnoRequestDTO;
import com.grupo73ISII.api_sistemaTurnos.dto.TurnoResponseDTO;
import com.grupo73ISII.api_sistemaTurnos.model.Cliente;
import com.grupo73ISII.api_sistemaTurnos.model.Reserva;
import com.grupo73ISII.api_sistemaTurnos.model.Turno;
import com.grupo73ISII.api_sistemaTurnos.repository.TurnoRepository;
import com.grupo73ISII.api_sistemaTurnos.service.impl.TurnoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TurnoServiceImplTest {

    @Mock private TurnoRepository turnoRepository;
    @Mock private IClienteService clienteService;
    @Mock private IReservaService reservaService;
    @Mock private IFacturacionService facturacionService;
    @Mock private IFranjaHorariaService franjaHorariaService;

    @InjectMocks
    private TurnoServiceImpl turnoService;

    private TurnoRequestDTO buildRequest(LocalDate fecha, Long idCliente, ReservaRequestDTO reserva) {
        TurnoRequestDTO dto = new TurnoRequestDTO();
        dto.setFechaTurno(fecha);
        dto.setIdCliente(idCliente);
        dto.setReservaRequest(reserva);
        return dto;
    }

    private ReservaRequestDTO buildReserva() {
        ReservaRequestDTO r = new ReservaRequestDTO();
        r.setIdCancha(5L);
        r.setIdFranjaHoraria("FH-1");
        r.setIdMetodoDePago(2L);
        return r;
    }

    // CASO 1: Datos válidos → Turno guardado OK
    @Test
    void procesarTurno_datosValidos_retornaTurnoGuardado() {
        Cliente cliente = Cliente.builder().idCliente(12L).suspendido(false).build();
        Reserva reserva = new Reserva();
        Turno turno = Turno.builder().idTurno(1L).build();

        when(clienteService.validarDatos(12L)).thenReturn(true);
        when(clienteService.findById(12L)).thenReturn(Optional.of(cliente));
        when(turnoRepository.existeTurnoActivo(any(), any(), any())).thenReturn(false);
        when(reservaService.procesarReserva(any())).thenReturn(reserva);
        when(turnoRepository.save(any())).thenReturn(turno);

        TurnoResponseDTO response = turnoService.procesarTurno(buildRequest(LocalDate.of(2026, 6, 10), 12L, buildReserva()));

        assertNotNull(response);
        assertEquals(1L, response.getIdTurno());
    }

    // CASO 2: Cliente no registrado
    @Test
    void procesarTurno_clienteNoRegistrado_lanzaExcepcion() {
        when(clienteService.validarDatos(999L)).thenReturn(true);
        when(clienteService.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            turnoService.procesarTurno(buildRequest(LocalDate.of(2026, 6, 10), 999L, buildReserva()))
        );
    }

    // CASO 3: Cliente suspendido
    @Test
    void procesarTurno_clienteSuspendido_lanzaExcepcion() {
        when(clienteService.validarDatos(12L)).thenReturn(false);

        assertThrows(RuntimeException.class, () ->
            turnoService.procesarTurno(buildRequest(LocalDate.of(2026, 6, 10), 12L, buildReserva()))
        );
    }

    // CASO 4: Fecha/hora ya ocupada
    @Test
    void procesarTurno_horarioOcupado_lanzaExcepcion() {
        Cliente cliente = Cliente.builder().idCliente(12L).build();
        when(clienteService.validarDatos(12L)).thenReturn(true);
        when(clienteService.findById(12L)).thenReturn(Optional.of(cliente));
        when(turnoRepository.existeTurnoActivo(any(), any(), any())).thenReturn(true);

        assertThrows(RuntimeException.class, () ->
            turnoService.procesarTurno(buildRequest(LocalDate.of(2026, 6, 10), 12L, buildReserva()))
        );
    }

    // CASO 5 y 6: Cancha o franja horaria no existe
    @Test
    void procesarTurno_canchaOFranjaNoExiste_lanzaExcepcion() {
        Cliente cliente = Cliente.builder().idCliente(12L).build();
        when(clienteService.validarDatos(12L)).thenReturn(true);
        when(clienteService.findById(12L)).thenReturn(Optional.of(cliente));
        when(turnoRepository.existeTurnoActivo(any(), any(), any())).thenReturn(false);
        when(reservaService.procesarReserva(any())).thenThrow(new RuntimeException("Cancha no encontrada"));

        assertThrows(RuntimeException.class, () ->
            turnoService.procesarTurno(buildRequest(LocalDate.of(2026, 6, 10), 12L, buildReserva()))
        );
    }

    // CASO 7: Fecha anterior a hoy
    @Test
    void procesarTurno_fechaAnteriorAHoy_lanzaExcepcion() {
        assertThrows(RuntimeException.class, () ->
            turnoService.procesarTurno(buildRequest(LocalDate.of(2020, 1, 1), 12L, buildReserva()))
        );
    }

    // CASO 8: fechaTurno nula
    @Test
    void procesarTurno_fechaNula_lanzaExcepcion() {
        assertThrows(RuntimeException.class, () ->
            turnoService.procesarTurno(buildRequest(null, 12L, buildReserva()))
        );
    }

    // CASO 9: idReserva nulo
    @Test
    void procesarTurno_reservaNula_lanzaExcepcion() {
        assertThrows(RuntimeException.class, () ->
            turnoService.procesarTurno(buildRequest(LocalDate.of(2026, 6, 10), 12L, null))
        );
    }

    // CASO 10: idCliente nulo
    @Test
    void procesarTurno_clienteNulo_lanzaExcepcion() {
        assertThrows(RuntimeException.class, () ->
            turnoService.procesarTurno(buildRequest(LocalDate.of(2026, 6, 10), null, buildReserva()))
        );
    }
}
