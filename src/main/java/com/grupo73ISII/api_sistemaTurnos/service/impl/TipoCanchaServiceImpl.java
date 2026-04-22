package com.grupo73ISII.api_sistemaTurnos.service.impl;

import com.grupo73ISII.api_sistemaTurnos.repository.TipoCanchaRepository;
import com.grupo73ISII.api_sistemaTurnos.service.ITipoCanchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TipoCanchaServiceImpl implements ITipoCanchaService {

    @Autowired
    private TipoCanchaRepository tipoCanchaRepository;

}
