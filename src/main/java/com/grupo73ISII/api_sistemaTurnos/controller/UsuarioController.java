package com.grupo73ISII.api_sistemaTurnos.controller;

import com.grupo73ISII.api_sistemaTurnos.dto.LoginRequestDTO;
import com.grupo73ISII.api_sistemaTurnos.dto.LoginResponseDTO;
import com.grupo73ISII.api_sistemaTurnos.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO response = usuarioService.login(loginRequestDTO);
        return ResponseEntity.ok(response);
    }
}
