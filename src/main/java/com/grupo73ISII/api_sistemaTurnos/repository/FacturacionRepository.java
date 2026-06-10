package com.grupo73ISII.api_sistemaTurnos.repository;

import com.grupo73ISII.api_sistemaTurnos.model.Facturacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacturacionRepository extends JpaRepository<Facturacion, Long> {
    Optional<Facturacion> findByPagoIdPago(Long idPago);
}
