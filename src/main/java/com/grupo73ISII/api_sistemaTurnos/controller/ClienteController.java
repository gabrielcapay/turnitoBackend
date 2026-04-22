package com.grupo73ISII.api_sistemaTurnos.controller;

import com.grupo73ISII.api_sistemaTurnos.dto.ClienteDetalleDTO;
import com.grupo73ISII.api_sistemaTurnos.model.Turno;
import com.grupo73ISII.api_sistemaTurnos.service.IClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cliente")
public class ClienteController {

    @Autowired
    private IClienteService clienteService;

    @GetMapping("/{idCliente}/turnos")
    public ResponseEntity<List<Turno>> obtenerTurnosPorCliente(@PathVariable Long idCliente) {
        List<Turno> turnos = clienteService.obtenerTurnosPorCliente(idCliente);
        return ResponseEntity.ok(turnos);
    }

    @GetMapping
    public ResponseEntity<List<ClienteDetalleDTO>> listarTodosLosClientes() {
        List<ClienteDetalleDTO> clientes = clienteService.listarTodosLosClientes();
        return ResponseEntity.ok(clientes);
    }
}
