package com.el_jumillano.pac.expected.infrastructure;

import com.el_jumillano.pac.expected.domain.ExpectedAmount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ExpectedAmountRepositoryAdapter {

    private final ExpectedAmountJpaRepository jpaRepository;
    private final ExpectedAmountMapper mapper;

    @Transactional
    public ExpectedAmount saveAsCurrentVersion(ExpectedAmount expectedAmount) {
        jpaRepository.markAllAsNotCurrent(
                expectedAmount.getRouteNumber(), expectedAmount.getPlantId(), expectedAmount.getDate());
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(expectedAmount)));
    }

    public Optional<ExpectedAmount> findCurrent(Integer routeNumber, Long plantId, LocalDate date) {
        return jpaRepository
                .findByRouteNumberAndPlantIdAndDateAndCurrentTrue(routeNumber, plantId, date)
                .map(mapper::toDomain);
    }
}
