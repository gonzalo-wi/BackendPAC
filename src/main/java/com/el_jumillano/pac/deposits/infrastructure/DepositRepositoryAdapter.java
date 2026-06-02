package com.el_jumillano.pac.deposits.infrastructure;

import com.el_jumillano.pac.deposits.domain.Deposit;
import com.el_jumillano.pac.deposits.domain.DepositAdjustment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DepositRepositoryAdapter {

    private final DepositJpaRepository jpaRepository;
    private final DepositAdjustmentJpaRepository adjustmentJpaRepository;
    private final DepositMapper mapper;

    public Deposit save(Deposit deposit) {
        return jpaRepository
                .findByExternalDepositIdAndCashierIdAndDepositDate(
                        deposit.getExternalDepositId(),
                        deposit.getCashierId(),
                        deposit.getDepositDate())
                .map(mapper::toDomain)
                .orElseGet(() -> mapper.toDomain(jpaRepository.save(mapper.toEntity(deposit))));
    }

    public Optional<Deposit> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    public List<Deposit> findByCashierAndDate(Long cashierId, LocalDate date) {
        return jpaRepository.findByCashierIdAndDepositDate(cashierId, date)
                .stream().map(mapper::toDomain).toList();
    }

    public List<Deposit> findByRouteAndPlantAndDate(Integer routeNumber, Long plantId, LocalDate date) {
        return jpaRepository.findByRouteNumberAndPlantIdAndDepositDate(routeNumber, plantId, date)
                .stream().map(mapper::toDomain).toList();
    }

    public BigDecimal sumByRouteAndPlantAndDate(Integer routeNumber, Long plantId, LocalDate date) {
        return jpaRepository.sumAmountByRouteAndPlantAndDate(routeNumber, plantId, date);
    }

    public DepositAdjustment saveAdjustment(DepositAdjustment adjustment) {
        return mapper.toAdjustmentDomain(
                adjustmentJpaRepository.save(mapper.toAdjustmentEntity(adjustment)));
    }

    public List<DepositAdjustment> findAdjustmentsByOriginalDepositId(Long originalDepositId) {
        return adjustmentJpaRepository.findByOriginalDepositId(originalDepositId)
                .stream().map(mapper::toAdjustmentDomain).toList();
    }
}
