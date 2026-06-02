package com.el_jumillano.pac.differences.infrastructure;

import com.el_jumillano.pac.differences.domain.DifferenceRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DifferenceRecordRepositoryAdapter {

    private final DifferenceRecordJpaRepository jpaRepository;
    private final DifferenceRecordMapper mapper;

    public DifferenceRecord save(DifferenceRecord record) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(record)));
    }

    public List<DifferenceRecord> findByReconciliationId(Long reconciliationId) {
        return jpaRepository.findByReconciliationId(reconciliationId)
                .stream().map(mapper::toDomain).toList();
    }

    public List<DifferenceRecord> findPendingNotification() {
        return jpaRepository.findByNotifiedFalse()
                .stream().map(mapper::toDomain).toList();
    }
}
