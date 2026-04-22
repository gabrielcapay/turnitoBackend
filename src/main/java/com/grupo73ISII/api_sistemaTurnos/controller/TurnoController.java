package com.grupo73ISII.api_sistemaTurnos.controller;

import com.grupo73ISII.api_sistemaTurnos.dto.DisponibilidadTurnoDTO;
import com.grupo73ISII.api_sistemaTurnos.dto.TurnoRequestDTO;
import com.grupo73ISII.api_sistemaTurnos.dto.TurnoResponseDTO;
import com.grupo73ISII.api_sistemaTurnos.dto.TurnoUpdateDTO;
import com.grupo73ISII.api_sistemaTurnos.model.Turno;
import com.grupo73ISII.api_sistemaTurnos.service.ITurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/turno")
public class TurnoController {

    @Autowired
    private ITurnoService turnoService;

    @PostMapping
    public ResponseEntity<TurnoResponseDTO> crearTurno(@RequestBody TurnoRequestDTO turnoRequestDTO) {
        TurnoResponseDTO nuevoTurno = turnoService.procesarTurno(turnoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTurno);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TurnoResponseDTO> editarTurno(@PathVariable Long id, @RequestBody TurnoUpdateDTO turnoUpdateDTO) {
        TurnoResponseDTO turnoActualizado = turnoService.editarTurno(id, turnoUpdateDTO);
        return ResponseEntity.ok(turnoActualizado);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarTurno(@PathVariable Long id) {
        turnoService.cancelarTurno(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/disponibilidad")
    public ResponseEntity<List<DisponibilidadTurnoDTO>> consultarDisponibilidad(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam Long idCancha) {
        List<DisponibilidadTurnoDTO> disponibilidad = turnoService.consultarDisponibilidad(fecha, idCancha);
        return ResponseEntity.ok(disponibilidad);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Turno>> listarTurnosActivos() {
        List<Turno> turnosActivos = turnoService.listarTurnosActivos();
        return ResponseEntity.ok(turnosActivos);
    }
}
