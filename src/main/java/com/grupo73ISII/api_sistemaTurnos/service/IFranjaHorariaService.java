package com.grupo73ISII.api_sistemaTurnos.service;

import com.grupo73ISII.api_sistemaTurnos.model.FranjaHoraria;
import java.util.List;
import java.util.Optional;

public interface IFranjaHorariaService {
    Optional<FranjaHoraria> findById(String id);
    List<FranjaHoraria> findAll();
}
