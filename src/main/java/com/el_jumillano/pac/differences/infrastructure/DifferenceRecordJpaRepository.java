package com.el_jumillano.pac.differences.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DifferenceRecordJpaRepository extends JpaRepository<DifferenceRecordJpaEntity, Long> {

    List<DifferenceRecordJpaEntity> findByReconciliationId(Long reconciliationId);

    List<DifferenceRecordJpaEntity> findByNotifiedFalse();
}
