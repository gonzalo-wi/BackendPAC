package com.el_jumillano.pac.plants.application;

import com.el_jumillano.pac.reconciliation.application.ProcessReconciliationUseCase;
import com.el_jumillano.pac.reconciliation.domain.Reconciliation;
import com.el_jumillano.pac.reconciliation.domain.ReconciliationStatus;
import com.el_jumillano.pac.reconciliation.infrastructure.ReconciliationRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessAllUseCase {

    private static final List<ReconciliationStatus> PROCESSABLE = List.of(
            ReconciliationStatus.PENDING,
            ReconciliationStatus.READY_TO_PROCESS,
            ReconciliationStatus.AWAITING_MANUAL_ITEMS,
            ReconciliationStatus.REQUIRES_REVIEW,
            ReconciliationStatus.INTEGRATION_ERROR,
            ReconciliationStatus.PROCESSED_WITH_SURPLUS,
            ReconciliationStatus.PROCESSED_WITH_SHORTAGE,
            ReconciliationStatus.PROCESSED_WITHOUT_DIFFERENCE
    );

    private final ReconciliationRepositoryAdapter reconciliationRepository;
    private final ProcessReconciliationUseCase processUseCase;

    public List<Reconciliation> execute(Long plantId, LocalDate date, String userId) {
        return reconciliationRepository.findByPlantAndDate(plantId, date).stream()
                .filter(rec -> PROCESSABLE.contains(rec.getStatus()))
                .map(rec -> {
                    try {
                        return processUseCase.execute(rec.getId(), userId);
                    } catch (Exception e) {
                        log.warn("[ProcessAll] Error en reparto {}: {}", rec.getRouteNumber(), e.getMessage());
                        return rec;
                    }
                })
                .toList();
    }
}
