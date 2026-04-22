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
    private Long id;

    private String numeroCancha;
    private BigDecimal precioHora;

    private Boolean estadoCancha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_cancha_id")
    private TipoCancha tipoCancha;
}
