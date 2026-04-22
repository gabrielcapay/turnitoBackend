package com.grupo73ISII.api_sistemaTurnos.service.impl;

import com.grupo73ISII.api_sistemaTurnos.model.Cancha;
import com.grupo73ISII.api_sistemaTurnos.repository.CanchaRepository;
import com.grupo73ISII.api_sistemaTurnos.service.ICanchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CanchaServiceImpl implements ICanchaService {

    @Autowired
    private CanchaRepository canchaRepository;

    @Override
    public BigDecimal obtenerPrecio(Long idCancha) {
        return canchaRepository.findById(idCancha)
                .map(Cancha::getPrecioHora)
                .orElseThrow(() -> new RuntimeException("Cancha no encontrada"));
    }

    @Override
    public Optional<Cancha> findById(Long id) {
        return canchaRepository.findById(id);
    }

    @Override
    public List<Cancha> findAll() {
        return canchaRepository.findAll();
    }
}
