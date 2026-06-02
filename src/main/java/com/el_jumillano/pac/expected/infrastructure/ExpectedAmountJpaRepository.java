package com.el_jumillano.pac.expected.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface ExpectedAmountJpaRepository extends JpaRepository<ExpectedAmountJpaEntity, Long> {

    Optional<ExpectedAmountJpaEntity> findByRouteNumberAndPlantIdAndDateAndCurrentTrue(
            Integer routeNumber, Long plantId, LocalDate date);

    @Modifying
    @Query("UPDATE ExpectedAmountJpaEntity e SET e.current = false " +
           "WHERE e.routeNumber = :routeNumber AND e.plantId = :plantId AND e.date = :date")
    void markAllAsNotCurrent(
            @Param("routeNumber") Integer routeNumber,
            @Param("plantId") Long plantId,
            @Param("date") LocalDate date);

    @Query("SELECT COALESCE(MAX(e.version), 0) FROM ExpectedAmountJpaEntity e " +
           "WHERE e.routeNumber = :routeNumber AND e.plantId = :plantId AND e.date = :date")
    Integer findMaxVersion(
            @Param("routeNumber") Integer routeNumber,
            @Param("plantId") Long plantId,
            @Param("date") LocalDate date);
}
