package com.grupo73ISII.api_sistemaTurnos.service.impl;

import com.grupo73ISII.api_sistemaTurnos.model.Pago;
import com.grupo73ISII.api_sistemaTurnos.repository.PagoRepository;
import com.grupo73ISII.api_sistemaTurnos.service.IPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PagoServiceImpl implements IPagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Override
    public Pago save(Pago pago) {
        return pagoRepository.save(pago);
    }
}
