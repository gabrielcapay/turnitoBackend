package com.grupo73ISII.api_sistemaTurnos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class WebhookController {

    @PostMapping("/api/pagos/webhook")
    public ResponseEntity<Void> recibirNotificacion(@RequestBody(required = false) String body,
                                                    @RequestParam Map<String, String> params) {
        System.out.println("=== Webhook MP Recibido ===");
        System.out.println("Body: " + body);
        System.out.println("Params: " + params);
        
        // Por ahora no procesamos nada, solo confirmamos recepción con un 200 OK
        return ResponseEntity.ok().build();
    }
}
