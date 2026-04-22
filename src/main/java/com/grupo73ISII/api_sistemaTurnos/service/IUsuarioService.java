package com.grupo73ISII.api_sistemaTurnos.service;

import com.grupo73ISII.api_sistemaTurnos.dto.LoginRequestDTO;
import com.grupo73ISII.api_sistemaTurnos.dto.LoginResponseDTO;

public interface IUsuarioService {
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
}
