package com.grupo73ISII.api_sistemaTurnos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private Long idUsuario;
    private String usuario;
    private Long idTipoUsuario;
    private String descripcionTipoUsuario;
    private Long idCliente; // Puede ser nulo
    private String nombre;
    private String apellido;
}
