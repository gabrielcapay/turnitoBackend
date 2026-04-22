package com.grupo73ISII.api_sistemaTurnos.service.impl;

import com.grupo73ISII.api_sistemaTurnos.dto.ClienteDetalleDTO;
import com.grupo73ISII.api_sistemaTurnos.dto.PersonaDetalleDTO;
import com.grupo73ISII.api_sistemaTurnos.model.Cliente;
import com.grupo73ISII.api_sistemaTurnos.model.Persona;
import com.grupo73ISII.api_sistemaTurnos.model.Turno;
import com.grupo73ISII.api_sistemaTurnos.repository.ClienteRepository;
import com.grupo73ISII.api_sistemaTurnos.service.IClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteServiceImpl implements IClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public boolean validarDatos(Long idCliente) {
        return !esSuspendido(idCliente);
    }

    @Override
    public boolean esSuspendido(Long idCliente) {
        return findById(idCliente)
                .map(Cliente::getSuspendido)
                .orElse(false);
    }

    @Override
    public Optional<Cliente> findById(Long idCliente) {
        return clienteRepository.findById(idCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Turno> obtenerTurnosPorCliente(Long idCliente) {
        return clienteRepository.findById(idCliente)
                .map(Cliente::getTurnos)
                .orElse(Collections.emptyList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDetalleDTO> listarTodosLosClientes() {
        return clienteRepository.findAll().stream()
                .map(this::convertirAClienteDetalleDTO)
                .collect(Collectors.toList());
    }

    private ClienteDetalleDTO convertirAClienteDetalleDTO(Cliente cliente) {
        Persona persona = cliente.getPersona();
        PersonaDetalleDTO personaDTO = null;
        if (persona != null) {
            personaDTO = new PersonaDetalleDTO(
                    persona.getNombre(),
                    persona.getApellido(),
                    persona.getEmail(),
                    persona.getTelefono()
            );
        }
        return new ClienteDetalleDTO(
                cliente.getIdCliente(),
                cliente.getInasistencias(),
                cliente.getSuspendido(),
                personaDTO
        );
    }
}
