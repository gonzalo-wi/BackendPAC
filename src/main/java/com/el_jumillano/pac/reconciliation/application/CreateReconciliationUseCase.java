package com.el_jumillano.pac.reconciliation.application;

import com.el_jumillano.pac.audit.application.AuditService;
import com.el_jumillano.pac.audit.domain.AuditAction;
import com.el_jumillano.pac.expected.infrastructure.ExpectedAmountRepositoryAdapter;
import com.el_jumillano.pac.plants.application.PlantResolverService;
import com.el_jumillano.pac.plants.infrastructure.PlantJpaRepository;
import com.el_jumillano.pac.reconciliation.domain.Reconciliation;
import com.el_jumillano.pac.reconciliation.domain.ReconciliationStatus;
import com.el_jumillano.pac.reconciliation.infrastructure.ReconciliationRepositoryAdapter;
import com.el_jumillano.pac.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateReconciliationUseCase {

    private final ReconciliationRepositoryAdapter reconciliationRepository;
    private final PlantJpaRepository plantRepo;
    private final PlantResolverService plantResolver;
    private final ExpectedAmountRepositoryAdapter expectedRepository;
    private final AuditService auditService;

    @Transactional
    public Reconciliation execute(Integer routeNumber, LocalDate date, String userId) {
        var plantCode = plantResolver.resolveByRouteNumber(routeNumber);
        var plant = plantRepo.findByCode(plantCode)
                .orElseThrow(() -> new EntityNotFoundException("Plant", plantCode));

        return reconciliationRepository
                .findByRouteAndPlantAndDate(routeNumber, plant.getId(), date)
                .orElseGet(() -> {
                    ReconciliationStatus initialStatus = resolveInitialStatus(routeNumber, plant.getId(), date);
                    log.info("[CreateReconciliation] routeNumber={} date={} planta={} status={}",
                            routeNumber, date, plantCode, initialStatus);
                    Reconciliation saved = reconciliationRepository.save(Reconciliation.builder()
                            .routeNumber(routeNumber)
                            .plantId(plant.getId())
                            .date(date)
                            .status(initialStatus)
                            .build());
                    auditService.log(AuditAction.RECONCILIATION_CREATED, "Reconciliation",
                            String.valueOf(saved.getId()),
                            null, "reparto=" + routeNumber + " fecha=" + date + " planta=" + plantCode,
                            userId);
                    return saved;
                });
    }

    private ReconciliationStatus resolveInitialStatus(Integer routeNumber, Long plantId, LocalDate date) {
        return expectedRepository.findCurrent(routeNumber, plantId, date)
                .filter(ea ->
                        isPositive(ea.getExpectedChecks()) || isPositive(ea.getExpectedWithholdings()))
                .map(ea -> ReconciliationStatus.AWAITING_MANUAL_ITEMS)
                .orElse(ReconciliationStatus.PENDING);
    }

    private boolean isPositive(java.math.BigDecimal value) {
        return value != null && value.compareTo(java.math.BigDecimal.ZERO) > 0;
    }
}
