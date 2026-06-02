package com.el_jumillano.pac.reconciliation.application;

import com.el_jumillano.pac.audit.application.AuditService;
import com.el_jumillano.pac.audit.domain.AuditAction;
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
    private final AuditService auditService;

    @Transactional
    public Reconciliation execute(Integer routeNumber, LocalDate date, String userId) {
        var plantCode = plantResolver.resolveByRouteNumber(routeNumber);
        var plant = plantRepo.findByCode(plantCode)
                .orElseThrow(() -> new EntityNotFoundException("Plant", plantCode));

        return reconciliationRepository
                .findByRouteAndPlantAndDate(routeNumber, plant.getId(), date)
                .orElseGet(() -> {
                    log.info("[CreateReconciliation] routeNumber={} date={} planta={}", routeNumber, date, plantCode);
                    Reconciliation saved = reconciliationRepository.save(Reconciliation.builder()
                            .routeNumber(routeNumber)
                            .plantId(plant.getId())
                            .date(date)
                            .status(ReconciliationStatus.PENDING)
                            .build());
                    auditService.log(AuditAction.RECONCILIATION_CREATED, "Reconciliation",
                            String.valueOf(saved.getId()),
                            null, "reparto=" + routeNumber + " fecha=" + date + " planta=" + plantCode,
                            userId);
                    return saved;
                });
    }
}
