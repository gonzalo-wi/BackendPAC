package com.el_jumillano.pac.plants.infrastructure;

import com.el_jumillano.pac.plants.domain.PlantCode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "plants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private PlantCode code;
}
