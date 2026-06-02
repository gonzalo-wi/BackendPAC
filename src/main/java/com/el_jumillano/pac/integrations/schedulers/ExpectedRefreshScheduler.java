package com.el_jumillano.pac.integrations.schedulers;

import com.el_jumillano.pac.expected.application.SyncAguasExpectedUseCase;
import com.el_jumillano.pac.reconciliation.domain.ReconciliationStatus;
import com.el_jumillano.pac.reconciliation.infrastructure.ReconciliationRepositoryAdapter;
import com.el_jumillano.pac.shared.exception.IntegrationUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpectedRefreshScheduler {

    private final ReconciliationRepositoryAdapter reconciliationRepo;
    private final SyncAguasExpectedUseCase syncUseCase;

    @Scheduled(fixedDelayString = "${pac.scheduler.aguas.fixed-delay-ms:600000}")
    public void refresh() {
        var pending = reconciliationRepo.findByStatuses(List.of(
                ReconciliationStatus.PENDING, ReconciliationStatus.READY_TO_PROCESS,
                ReconciliationStatus.REQUIRES_REVIEW));

        pending.forEach(rec -> {
            try {
                syncUseCase.execute(rec.getDate(), rec.getRouteNumber());
            } catch (IntegrationUnavailableException e) {
                log.error("Aguas no disponible al refrescar reparto {}/{}: {}",
                        rec.getRouteNumber(), rec.getDate(), e.getMessage());
            } catch (Exception e) {
                log.error("Error refrescando esperado reparto {}/{}: {}",
                        rec.getRouteNumber(), rec.getDate(), e.getMessage());
            }
        });
    }
}
