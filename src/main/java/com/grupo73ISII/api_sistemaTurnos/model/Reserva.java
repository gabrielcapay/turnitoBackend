package com.grupo73ISII.api_sistemaTurnos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "reserva")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long idReserva;

    @Column(name = "montoapagar")
    private BigDecimal montoAPagar;

    @OneToOne
    @JoinColumn(name = "id_facturacion")
    private Facturacion facturacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_franja_horaria")
    private FranjaHoraria franjaHoraria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cancha")
    private Cancha cancha;
}
