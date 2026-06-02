package com.el_jumillano.pac.reconciliation.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ManualValuesJpaRepository extends JpaRepository<ManualValuesJpaEntity, Long> {

    Optional<ManualValuesJpaEntity> findByRouteNumberAndPlantIdAndDate(
            Integer routeNumber, Long plantId, LocalDate date);
}
