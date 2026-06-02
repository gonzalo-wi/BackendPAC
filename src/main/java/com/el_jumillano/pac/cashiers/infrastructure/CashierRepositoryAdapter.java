package com.el_jumillano.pac.cashiers.infrastructure;

import com.el_jumillano.pac.cashiers.domain.Cashier;
import com.el_jumillano.pac.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CashierRepositoryAdapter {

    private final CashierJpaRepository jpaRepository;
    private final CashierMapper mapper;

    public List<Cashier> findActiveByPlantId(Long plantId) {
        return jpaRepository.findByPlantIdAndActiveTrue(plantId)
                .stream().map(mapper::toDomain).toList();
    }

    public Optional<Cashier> findByExternalCashierNumber(Integer externalNumber) {
        return jpaRepository.findByExternalCashierNumber(externalNumber)
                .map(mapper::toDomain);
    }

    public Cashier getByExternalCashierNumber(Integer externalNumber) {
        return findByExternalCashierNumber(externalNumber)
                .orElseThrow(() -> new EntityNotFoundException("Cashier", externalNumber));
    }
}
