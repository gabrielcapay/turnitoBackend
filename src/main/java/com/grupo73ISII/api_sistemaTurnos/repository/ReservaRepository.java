package com.grupo73ISII.api_sistemaTurnos.repository;

import com.grupo73ISII.api_sistemaTurnos.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Procedure(procedureName = "sp_registrar_reserva")
    Long registrarReservaSP(
            @Param("p_monto") BigDecimal monto,
            @Param("p_id_facturacion") Long idFacturacion,
            @Param("p_id_cancha") Long idCancha,
            @Param("p_id_franja") String idFranjaHoraria);
}
