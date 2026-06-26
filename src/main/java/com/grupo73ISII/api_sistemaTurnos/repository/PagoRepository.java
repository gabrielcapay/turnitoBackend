package com.grupo73ISII.api_sistemaTurnos.repository;

import com.grupo73ISII.api_sistemaTurnos.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    @Procedure(procedureName = "sp_registrar_pago")
    void registrarPagoSP(
            @Param("p_id_pago") Long idPago,
            @Param("p_numero_transaccion") String numeroTransaccion);
}
