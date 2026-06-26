package com.grupo73ISII.api_sistemaTurnos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cancha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cancha")
    private Long idCancha;

    @Column(name = "numero_cancha")
    private String numeroCancha;

    @Column(name = "precio_hora")
    private BigDecimal precioHora;

    @Column(name = "estado_cancha")
    private Boolean estadoCancha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_cancha_id")
    private TipoCancha tipoCancha;
}
