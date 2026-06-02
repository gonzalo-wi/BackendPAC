package com.el_jumillano.pac.reconciliation.infrastructure;

import com.el_jumillano.pac.reconciliation.domain.ReconciliationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReconciliationJpaRepository extends JpaRepository<ReconciliationJpaEntity, Long> {

    Optional<ReconciliationJpaEntity> findByRouteNumberAndPlantIdAndDate(
            Integer routeNumber, Long plantId, LocalDate date);

    List<ReconciliationJpaEntity> findByPlantIdAndDate(Long plantId, LocalDate date);

    List<ReconciliationJpaEntity> findByStatusIn(List<ReconciliationStatus> statuses);
}
