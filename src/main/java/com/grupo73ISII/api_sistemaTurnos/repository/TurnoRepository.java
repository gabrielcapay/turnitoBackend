package com.grupo73ISII.api_sistemaTurnos.repository;

import com.grupo73ISII.api_sistemaTurnos.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    @Query("SELECT t FROM Turno t JOIN t.reserva r JOIN r.cancha c " +
           "WHERE t.reserva.franjaHoraria.id_franjaHoraria = :idFranjaHoraria " +
           "AND t.fechaTurno = :fechaTurno " +
           "AND c.id = :idCancha " +
           "AND t.estadoTurno = true")
    Optional<Turno> findTurnoByFranjaFechaAndCancha(
            @Param("idFranjaHoraria") String idFranjaHoraria,
            @Param("fechaTurno") LocalDate fechaTurno,
            @Param("idCancha") Long idCancha);

    @Query("SELECT t FROM Turno t JOIN t.reserva r JOIN r.cancha c WHERE t.fechaTurno = :fecha AND c.id = :idCancha AND t.estadoTurno = true")
    List<Turno> findActiveTurnosByFechaAndCancha(@Param("fecha") LocalDate fecha, @Param("idCancha") Long idCancha);

    List<Turno> findAllByEstadoTurno(boolean estado);

    Optional<Turno> findByReserva_Facturacion_IdFacturacion(Long idFacturacion);
}
