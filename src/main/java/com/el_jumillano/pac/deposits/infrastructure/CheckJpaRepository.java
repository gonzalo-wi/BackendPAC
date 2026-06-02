package com.el_jumillano.pac.deposits.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface CheckJpaRepository extends JpaRepository<CheckJpaEntity, Long> {

    List<CheckJpaEntity> findByDepositId(Long depositId);

    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM CheckJpaEntity c WHERE c.deposit.id = :depositId")
    BigDecimal sumAmountByDepositId(@Param("depositId") Long depositId);

    @Query("""
            SELECT COALESCE(SUM(c.amount), 0)
            FROM CheckJpaEntity c
            WHERE c.deposit.routeNumber = :routeNumber
              AND c.deposit.plantId     = :plantId
              AND c.deposit.depositDate = :date
            """)
    BigDecimal sumAmountByRouteAndPlantAndDate(
            @Param("routeNumber") Integer routeNumber,
            @Param("plantId")     Long plantId,
            @Param("date")        LocalDate date);
}
