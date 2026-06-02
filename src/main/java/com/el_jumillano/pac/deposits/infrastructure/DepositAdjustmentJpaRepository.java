package com.el_jumillano.pac.deposits.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepositAdjustmentJpaRepository extends JpaRepository<DepositAdjustmentJpaEntity, Long> {

    List<DepositAdjustmentJpaEntity> findByOriginalDepositId(Long originalDepositId);

    List<DepositAdjustmentJpaEntity> findByToRouteNumber(Integer toRouteNumber);
}
