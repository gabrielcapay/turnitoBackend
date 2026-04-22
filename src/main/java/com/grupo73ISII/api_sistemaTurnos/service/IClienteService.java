package com.grupo73ISII.api_sistemaTurnos.service;

import com.grupo73ISII.api_sistemaTurnos.dto.ClienteDetalleDTO;
import com.grupo73ISII.api_sistemaTurnos.model.Cliente;
import com.grupo73ISII.api_sistemaTurnos.model.Turno;
import java.util.List;
import java.util.Optional;

public interface IClienteService {
    boolean validarDatos(Long idCliente);
    boolean esSuspendido(Long idCliente);
    Optional<Cliente> findById(Long idCliente);
    List<Turno> obtenerTurnosPorCliente(Long idCliente);
    List<ClienteDetalleDTO> listarTodosLosClientes();
}
