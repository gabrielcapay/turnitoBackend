package com.grupo73ISII.api_sistemaTurnos.service.impl;

import com.grupo73ISII.api_sistemaTurnos.model.EstadoFacturacion;
import com.grupo73ISII.api_sistemaTurnos.repository.EstadoFacturacionRepository;
import com.grupo73ISII.api_sistemaTurnos.service.IEstadoFacturacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class EstadoFacturacionServiceImpl implements IEstadoFacturacionService {

    @Autowired
    private EstadoFacturacionRepository estadoFacturacionRepository;

    @Override
    public Optional<EstadoFacturacion> findById(Long id) {
        return estadoFacturacionRepository.findById(id);
    }
}
