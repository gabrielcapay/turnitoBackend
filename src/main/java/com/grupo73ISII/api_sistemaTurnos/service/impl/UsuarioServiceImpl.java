package com.grupo73ISII.api_sistemaTurnos.service.impl;

import com.grupo73ISII.api_sistemaTurnos.dto.LoginRequestDTO;
import com.grupo73ISII.api_sistemaTurnos.dto.LoginResponseDTO;
import com.grupo73ISII.api_sistemaTurnos.model.Cliente;
import com.grupo73ISII.api_sistemaTurnos.model.Usuario;
import com.grupo73ISII.api_sistemaTurnos.repository.UsuarioRepository;
import com.grupo73ISII.api_sistemaTurnos.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        // 1. Buscar al usuario por su nombre de usuario
        Usuario usuario = usuarioRepository.findByUsuario(loginRequestDTO.getUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario o contraseña incorrectos"));

        // 2. Comparar la contraseña (en texto plano, como se solicitó)
        if (!usuario.getPassword().equals(loginRequestDTO.getPassword())) {
            throw new RuntimeException("Usuario o contraseña incorrectos");
        }

        // 3. Si las credenciales son correctas, mapear al DTO de respuesta
        LoginResponseDTO response = new LoginResponseDTO();
        response.setIdUsuario(usuario.getId());
        response.setUsuario(usuario.getUsuario());
        response.setIdTipoUsuario(usuario.getTipoUsuario().getId());
        response.setDescripcionTipoUsuario(usuario.getTipoUsuario().getDescripcion());

        // Si el usuario es un cliente, obtener sus datos personales
        if (usuario.getCliente() != null) {
            Cliente cliente = usuario.getCliente();
            response.setIdCliente(cliente.getIdCliente());
            if (cliente.getPersona() != null) {
                response.setNombre(cliente.getPersona().getNombre());
                response.setApellido(cliente.getPersona().getApellido());
            }
        }

        return response;
    }
}
