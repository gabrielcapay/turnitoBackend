package com.grupo73ISII.api_sistemaTurnos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "metodo_de_pago")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetodoDePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metodo_pago")
    private Long idMetodoPago;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(nullable = false, columnDefinition = "double default 0.0")
    private double recargo;
}
