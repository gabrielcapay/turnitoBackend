package com.grupo73ISII.api_sistemaTurnos.service;

import com.grupo73ISII.api_sistemaTurnos.model.EstadoFacturacion;
import java.util.Optional;

public interface IEstadoFacturacionService {
    Optional<EstadoFacturacion> findById(Long id);
}
