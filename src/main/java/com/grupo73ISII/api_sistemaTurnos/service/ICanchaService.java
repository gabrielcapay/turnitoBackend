package com.grupo73ISII.api_sistemaTurnos.service;

import com.grupo73ISII.api_sistemaTurnos.model.Cancha;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ICanchaService {
    BigDecimal obtenerPrecio(Long idCancha);
    Optional<Cancha> findById(Long id);
    List<Cancha> findAll();
}
