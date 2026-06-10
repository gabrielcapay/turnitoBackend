package com.grupo73ISII.api_sistemaTurnos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "facturacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facturacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFacturacion;

    private LocalDateTime fechaRegistro;

    private String emailFacturacion;

    private String telefonoFacturacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado_facturacion")
    private EstadoFacturacion estadoFacturacion;

    @OneToOne
    @JoinColumn(name = "id_pago")
    private Pago pago;
}
