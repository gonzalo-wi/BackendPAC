package com.el_jumillano.pac.routes.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RouteJpaRepository extends JpaRepository<RouteJpaEntity, Long> {

    Optional<RouteJpaEntity> findByRouteNumberAndDate(Integer routeNumber, LocalDate date);

    List<RouteJpaEntity> findByPlantIdAndDate(Long plantId, LocalDate date);
}
