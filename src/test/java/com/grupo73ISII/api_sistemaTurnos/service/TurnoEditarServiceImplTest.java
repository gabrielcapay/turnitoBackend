package com.grupo73ISII.api_sistemaTurnos.service;

import com.grupo73ISII.api_sistemaTurnos.dto.TurnoResponseDTO;
import com.grupo73ISII.api_sistemaTurnos.dto.TurnoUpdateDTO;
import com.grupo73ISII.api_sistemaTurnos.model.Cliente;
import com.grupo73ISII.api_sistemaTurnos.model.Facturacion;
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
class TurnoEditarServiceImplTest {

    @Mock private TurnoRepository turnoRepository;
    @Mock private IClienteService clienteService;
    @Mock private IReservaService reservaService;
    @Mock private IFacturacionService facturacionService;
    @Mock private IFranjaHorariaService franjaHorariaService;

    @InjectMocks
    private TurnoServiceImpl turnoService;

    private TurnoUpdateDTO buildUpdate(LocalDate fecha, Long idCancha, String idFranja) {
        TurnoUpdateDTO dto = new TurnoUpdateDTO();
        dto.setNuevaFechaTurno(fecha);
        dto.setNuevoIdCancha(idCancha);
        dto.setNuevoIdFranjaHoraria(idFranja);
        return dto;
    }

    private Turno buildTurnoActivo(Long idTurno, Long idCliente, Long idFacturacion) {
        Cliente cliente = Cliente.builder().idCliente(idCliente).build();
        Facturacion facturacion = Facturacion.builder().idFacturacion(idFacturacion).build();
        Reserva reserva = Reserva.builder().facturacion(facturacion).build();
        return Turno.builder()
                .idTurno(idTurno)
                .estadoTurno(true)
                .cliente(cliente)
                .reserva(reserva)
                .build();
    }

    // CASO 1: Datos válidos → Turno modificado OK
    @Test
    void editarTurno_datosValidos_retornaTurnoModificado() {
        Turno turno = buildTurnoActivo(8L, 12L, 1L);
        Turno turnoModificado = Turno.builder().idTurno(8L).build();

        when(turnoRepository.findById(8L)).thenReturn(Optional.of(turno));
        when(clienteService.validarDatos(12L)).thenReturn(true);
        when(clienteService.findById(12L)).thenReturn(Optional.of(turno.getCliente()));
        when(turnoRepository.existeTurnoActivo(any(), any(), any())).thenReturn(false);
        when(facturacionService.estaPagada(1L)).thenReturn(true);
        when(turnoRepository.save(any())).thenReturn(turnoModificado);

        TurnoResponseDTO response = turnoService.editarTurno(8L, buildUpdate(LocalDate.of(2026, 6, 15), 3L, "2"));

        assertNotNull(response);
        assertEquals(8L, response.getIdTurno());
    }

    // CASO 2: Turno cancelado
    @Test
    void editarTurno_turnoCancelado_lanzaExcepcion() {
        Turno turno = buildTurnoActivo(8L, 12L, 1L);
        turno.setEstadoTurno(false);

        when(turnoRepository.findById(8L)).thenReturn(Optional.of(turno));

        assertThrows(RuntimeException.class, () ->
            turnoService.editarTurno(8L, buildUpdate(LocalDate.of(2026, 6, 15), 3L, "2"))
        );
    }

    // CASO 3: Turno no existe
    @Test
    void editarTurno_turnoNoExiste_lanzaExcepcion() {
        when(turnoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            turnoService.editarTurno(999L, buildUpdate(LocalDate.of(2026, 6, 15), 3L, "2"))
        );
    }

    // CASO 4: Cliente suspendido o no registrado
    @Test
    void editarTurno_clienteSuspendido_lanzaExcepcion() {
        Turno turno = buildTurnoActivo(8L, 12L, 1L);

        when(turnoRepository.findById(8L)).thenReturn(Optional.of(turno));
        when(clienteService.validarDatos(12L)).thenReturn(false);

        assertThrows(RuntimeException.class, () ->
            turnoService.editarTurno(8L, buildUpdate(LocalDate.of(2026, 6, 15), 3L, "2"))
        );
    }

    // CASO 5: Reserva no disponible
    @Test
    void editarTurno_reservaNoDisponible_lanzaExcepcion() {
        Turno turno = buildTurnoActivo(8L, 12L, 1L);

        when(turnoRepository.findById(8L)).thenReturn(Optional.of(turno));
        when(clienteService.validarDatos(12L)).thenReturn(true);
        when(clienteService.findById(12L)).thenReturn(Optional.of(turno.getCliente()));
        when(turnoRepository.existeTurnoActivo(any(), any(), any())).thenReturn(true);

        assertThrows(RuntimeException.class, () ->
            turnoService.editarTurno(8L, buildUpdate(LocalDate.of(2026, 6, 15), 3L, "2"))
        );
    }

    // CASO 6: Reserva no está paga
    @Test
    void editarTurno_reservaNoPaga_lanzaExcepcion() {
        Turno turno = buildTurnoActivo(8L, 12L, 1L);

        when(turnoRepository.findById(8L)).thenReturn(Optional.of(turno));
        when(clienteService.validarDatos(12L)).thenReturn(true);
        when(clienteService.findById(12L)).thenReturn(Optional.of(turno.getCliente()));
        when(turnoRepository.existeTurnoActivo(any(), any(), any())).thenReturn(false);
        when(facturacionService.estaPagada(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () ->
            turnoService.editarTurno(8L, buildUpdate(LocalDate.of(2026, 6, 15), 3L, "2"))
        );
    }

    // CASO 7: Nueva fecha anterior a hoy
    @Test
    void editarTurno_fechaAnteriorAHoy_lanzaExcepcion() {
        assertThrows(RuntimeException.class, () ->
            turnoService.editarTurno(8L, buildUpdate(LocalDate.of(2020, 1, 1), 3L, "2"))
        );
    }

    // CASO 8: nuevaFecha nula
    @Test
    void editarTurno_fechaNula_lanzaExcepcion() {
        assertThrows(RuntimeException.class, () ->
            turnoService.editarTurno(8L, buildUpdate(null, 3L, "2"))
        );
    }

    // CASO 9: idTurno nulo
    @Test
    void editarTurno_idTurnoNulo_lanzaExcepcion() {
        assertThrows(RuntimeException.class, () ->
            turnoService.editarTurno(null, buildUpdate(LocalDate.of(2026, 6, 15), 3L, "2"))
        );
    }

    // CASO 10: idCancha nula
    @Test
    void editarTurno_idCanchaNula_lanzaExcepcion() {
        assertThrows(RuntimeException.class, () ->
            turnoService.editarTurno(8L, buildUpdate(LocalDate.of(2026, 6, 15), null, "2"))
        );
    }

    // CASO 11: idFranjaHoraria nula
    @Test
    void editarTurno_idFranjaHorariaNula_lanzaExcepcion() {
        assertThrows(RuntimeException.class, () ->
            turnoService.editarTurno(8L, buildUpdate(LocalDate.of(2026, 6, 15), 3L, null))
        );
    }
}
