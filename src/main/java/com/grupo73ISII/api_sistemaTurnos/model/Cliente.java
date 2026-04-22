package com.grupo73ISII.api_sistemaTurnos.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCliente;

    private Integer inasistencias;
    private Boolean suspendido;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dato_persona", unique = true)
    private Persona persona;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonManagedReference
    private List<Turno> turnos = new ArrayList<>();
}
