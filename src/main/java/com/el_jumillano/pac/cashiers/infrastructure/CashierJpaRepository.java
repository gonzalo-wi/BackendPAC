package com.el_jumillano.pac.cashiers.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CashierJpaRepository extends JpaRepository<CashierJpaEntity, Long> {

    Optional<CashierJpaEntity> findByExternalCashierNumber(Integer externalCashierNumber);

    List<CashierJpaEntity> findByPlantIdAndActiveTrue(Long plantId);
}
