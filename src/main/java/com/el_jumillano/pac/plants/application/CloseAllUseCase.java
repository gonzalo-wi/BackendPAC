package com.el_jumillano.pac.plants.application;

import com.el_jumillano.pac.reconciliation.application.CloseReconciliationUseCase;
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
public class CloseAllUseCase {

    private static final List<ReconciliationStatus> CLOSEABLE = List.of(
            ReconciliationStatus.PROCESSED_WITH_SURPLUS,
            ReconciliationStatus.PROCESSED_WITH_SHORTAGE,
            ReconciliationStatus.PROCESSED_WITHOUT_DIFFERENCE
    );

    private final ReconciliationRepositoryAdapter reconciliationRepository;
    private final CloseReconciliationUseCase closeUseCase;

    public List<Reconciliation> execute(Long plantId, LocalDate date, String userId) {
        return reconciliationRepository.findByPlantAndDate(plantId, date).stream()
                .filter(rec -> CLOSEABLE.contains(rec.getStatus()))
                .map(rec -> {
                    try {
                        return closeUseCase.execute(rec.getId(), userId);
                    } catch (Exception e) {
                        log.warn("[CloseAll] Error cerrando reparto {}: {}", rec.getRouteNumber(), e.getMessage());
                        return rec;
                    }
                })
                .toList();
    }
}
