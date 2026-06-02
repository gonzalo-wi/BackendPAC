package com.el_jumillano.pac.routes.infrastructure;

import com.el_jumillano.pac.routes.domain.RouteStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "routes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_number", nullable = false)
    private Integer routeNumber;

    @Column(name = "plant_id", nullable = false)
    private Long plantId;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RouteStatus status;
}
