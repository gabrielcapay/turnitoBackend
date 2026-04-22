package com.grupo73ISII.api_sistemaTurnos.service;

import com.grupo73ISII.api_sistemaTurnos.model.MetodoDePago;
import java.util.Optional;

public interface IMetodoDePagoService {
    Optional<MetodoDePago> findById(Long id);
}
