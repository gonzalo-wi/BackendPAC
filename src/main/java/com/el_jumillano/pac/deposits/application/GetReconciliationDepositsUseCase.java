package com.el_jumillano.pac.deposits.application;

import com.el_jumillano.pac.deposits.domain.Deposit;
import com.el_jumillano.pac.deposits.infrastructure.DepositRepositoryAdapter;
import com.el_jumillano.pac.reconciliation.infrastructure.ReconciliationRepositoryAdapter;
import com.el_jumillano.pac.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetReconciliationDepositsUseCase {

    private final ReconciliationRepositoryAdapter reconciliationRepository;
    private final DepositRepositoryAdapter depositRepository;

    public List<Deposit> execute(Long reconciliationId) {
        var rec = reconciliationRepository.findById(reconciliationId)
                .orElseThrow(() -> new EntityNotFoundException("Reconciliation", reconciliationId));
        return depositRepository.findByRouteAndPlantAndDate(
                rec.getRouteNumber(), rec.getPlantId(), rec.getDate());
    }
}
