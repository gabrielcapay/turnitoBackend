package com.grupo73ISII.api_sistemaTurnos.service.impl;

import com.grupo73ISII.api_sistemaTurnos.model.MetodoDePago;
import com.grupo73ISII.api_sistemaTurnos.repository.MetodoDePagoRepository;
import com.grupo73ISII.api_sistemaTurnos.service.IMetodoDePagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class MetodoDePagoServiceImpl implements IMetodoDePagoService {

    @Autowired
    private MetodoDePagoRepository metodoDePagoRepository;

    @Override
    public Optional<MetodoDePago> findById(Long id) {
        return metodoDePagoRepository.findById(id);
    }
}
