package com.el_jumillano.pac.deposits.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface WithholdingJpaRepository extends JpaRepository<WithholdingJpaEntity, Long> {

    List<WithholdingJpaEntity> findByDepositId(Long depositId);

    @Query("""
            SELECT w FROM WithholdingJpaEntity w
            WHERE w.deposit.routeNumber = :routeNumber
              AND w.deposit.plantId     = :plantId
              AND w.deposit.depositDate = :date
            """)
    List<WithholdingJpaEntity> findByRouteAndPlantAndDate(
            @Param("routeNumber") Integer routeNumber,
            @Param("plantId")     Long plantId,
            @Param("date")        LocalDate date);

    @Query("SELECT COALESCE(SUM(w.amount), 0) FROM WithholdingJpaEntity w WHERE w.deposit.id = :depositId")
    BigDecimal sumAmountByDepositId(@Param("depositId") Long depositId);

    @Query("""
            SELECT COALESCE(SUM(w.amount), 0)
            FROM WithholdingJpaEntity w
            WHERE w.deposit.routeNumber = :routeNumber
              AND w.deposit.plantId     = :plantId
              AND w.deposit.depositDate = :date
            """)
    BigDecimal sumAmountByRouteAndPlantAndDate(
            @Param("routeNumber") Integer routeNumber,
            @Param("plantId")     Long plantId,
            @Param("date")        LocalDate date);
}
