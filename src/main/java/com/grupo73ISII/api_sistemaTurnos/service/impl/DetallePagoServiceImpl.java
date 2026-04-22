package com.grupo73ISII.api_sistemaTurnos.service.impl;

import com.grupo73ISII.api_sistemaTurnos.model.DetallePago;
import com.grupo73ISII.api_sistemaTurnos.repository.DetallePagoRepository;
import com.grupo73ISII.api_sistemaTurnos.service.IDetallePagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DetallePagoServiceImpl implements IDetallePagoService {

    @Autowired
    private DetallePagoRepository detallePagoRepository;

    @Override
    public DetallePago save(DetallePago detallePago) {
        return detallePagoRepository.save(detallePago);
    }
}
