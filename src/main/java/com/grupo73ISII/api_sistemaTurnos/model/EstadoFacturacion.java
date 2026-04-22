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
    private Long idEstadoFacturacion;

    private String descripcion;
}
