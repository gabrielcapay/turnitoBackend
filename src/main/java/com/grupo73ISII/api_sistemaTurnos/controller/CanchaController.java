package com.grupo73ISII.api_sistemaTurnos.controller;

import com.grupo73ISII.api_sistemaTurnos.model.Cancha;
import com.grupo73ISII.api_sistemaTurnos.service.ICanchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cancha")
public class CanchaController {

    @Autowired
    private ICanchaService canchaService;

    @GetMapping
    public ResponseEntity<List<Cancha>> listarCanchas() {
        List<Cancha> canchas = canchaService.findAll();
        return ResponseEntity.ok(canchas);
    }
}
