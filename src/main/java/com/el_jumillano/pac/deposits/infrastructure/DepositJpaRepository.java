package com.el_jumillano.pac.deposits.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DepositJpaRepository extends JpaRepository<DepositJpaEntity, Long> {

    Optional<DepositJpaEntity> findByExternalDepositIdAndCashierIdAndDepositDate(
            String externalDepositId, Long cashierId, LocalDate depositDate);

    List<DepositJpaEntity> findByCashierIdAndDepositDate(Long cashierId, LocalDate depositDate);

    List<DepositJpaEntity> findByRouteNumberAndPlantIdAndDepositDate(
            Integer routeNumber, Long plantId, LocalDate depositDate);

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM DepositJpaEntity d " +
           "WHERE d.routeNumber = :routeNumber AND d.plantId = :plantId AND d.depositDate = :date")
    BigDecimal sumAmountByRouteAndPlantAndDate(
            @Param("routeNumber") Integer routeNumber,
            @Param("plantId") Long plantId,
            @Param("date") LocalDate date);
}
