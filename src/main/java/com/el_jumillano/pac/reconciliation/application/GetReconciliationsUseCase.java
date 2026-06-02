package com.el_jumillano.pac.reconciliation.application;

import com.el_jumillano.pac.reconciliation.domain.Reconciliation;
import com.el_jumillano.pac.reconciliation.infrastructure.ReconciliationRepositoryAdapter;
import com.el_jumillano.pac.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetReconciliationsUseCase {

    private final ReconciliationRepositoryAdapter repository;

    public List<Reconciliation> getByPlantAndDate(Long plantId, LocalDate date) {
        return repository.findByPlantAndDate(plantId, date);
    }

    public Reconciliation getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reconciliation", id));
    }
}
