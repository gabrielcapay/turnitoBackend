package com.grupo73ISII.api_sistemaTurnos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado_facturacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoFacturacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_facturacion")
    private Long idEstadoFacturacion;

    @Column(name = "descripcion")
    private String descripcion;
}
