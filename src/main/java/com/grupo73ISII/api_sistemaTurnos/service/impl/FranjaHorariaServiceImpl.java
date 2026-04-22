package com.grupo73ISII.api_sistemaTurnos.service.impl;

import com.grupo73ISII.api_sistemaTurnos.model.FranjaHoraria;
import com.grupo73ISII.api_sistemaTurnos.repository.FranjaHorariaRepository;
import com.grupo73ISII.api_sistemaTurnos.service.IFranjaHorariaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FranjaHorariaServiceImpl implements IFranjaHorariaService {

    @Autowired
    private FranjaHorariaRepository franjaHorariaRepository;

    @Override
    public Optional<FranjaHoraria> findById(String id) {
        return franjaHorariaRepository.findById(id);
    }

    @Override
    public List<FranjaHoraria> findAll() {
        return franjaHorariaRepository.findAll();
    }
}
