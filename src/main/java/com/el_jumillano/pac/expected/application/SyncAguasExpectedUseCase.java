package com.el_jumillano.pac.expected.application;

import com.el_jumillano.pac.audit.application.AuditService;
import com.el_jumillano.pac.audit.domain.AuditAction;
import com.el_jumillano.pac.expected.domain.ExpectedAmount;
import com.el_jumillano.pac.expected.infrastructure.ExpectedAmountRepositoryAdapter;
import com.el_jumillano.pac.integrations.aguas.AguasClient;
import com.el_jumillano.pac.integrations.health.IntegrationHealthStatus;
import com.el_jumillano.pac.integrations.health.IntegrationProvider;
import com.el_jumillano.pac.integrations.health.IntegrationStatus;
import com.el_jumillano.pac.integrations.health.IntegrationStatusRepositoryAdapter;
import com.el_jumillano.pac.plants.application.PlantResolverService;
import com.el_jumillano.pac.plants.domain.PlantCode;
import com.el_jumillano.pac.plants.infrastructure.PlantJpaRepository;
import com.el_jumillano.pac.reconciliation.domain.ReconciliationStatus;
import com.el_jumillano.pac.reconciliation.infrastructure.ReconciliationRepositoryAdapter;
import com.el_jumillano.pac.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncAguasExpectedUseCase {

    private final AguasClient aguasClient;
    private final ExpectedAmountRepositoryAdapter expectedRepository;
    private final ReconciliationRepositoryAdapter reconciliationRepository;
    private final PlantJpaRepository plantRepo;
    private final PlantResolverService plantResolver;
    private final IntegrationStatusRepositoryAdapter integrationStatusRepo;
    private final AuditService auditService;

    private static final List<ReconciliationStatus> FROZEN_STATUSES = List.of(
            ReconciliationStatus.PROCESSED_WITH_SURPLUS,
            ReconciliationStatus.PROCESSED_WITH_SHORTAGE,
            ReconciliationStatus.PROCESSED_WITHOUT_DIFFERENCE,
            ReconciliationStatus.CLOSED
    );

    @Transactional
    public ExpectedAmount execute(LocalDate date, Integer routeNumber) {
        // Si ya está procesado/cerrado, no actualizar
        PlantCode plantCode = plantResolver.resolveByRouteNumber(routeNumber);
        var plantEntity = plantRepo.findByCode(plantCode)
                .orElseThrow(() -> new EntityNotFoundException("Plant", plantCode));

        var existing = reconciliationRepository.findByRouteAndPlantAndDate(
                routeNumber, plantEntity.getId(), date);
        if (existing.isPresent() && FROZEN_STATUSES.contains(existing.get().getStatus())) {
            log.info("Reparto {}/{} ya procesado. Esperado congelado.", routeNumber, date);
            return expectedRepository.findCurrent(routeNumber, plantEntity.getId(), date)
                    .orElseThrow(() -> new EntityNotFoundException("ExpectedAmount", routeNumber));
        }

        var previous = expectedRepository.findCurrent(routeNumber, plantEntity.getId(), date);

        ExpectedAmount fetched;
        try {
            fetched = aguasClient.getExpectedByRoute(date, routeNumber);
            integrationStatusRepo.upsertStatus(IntegrationStatus.builder()
                    .provider(IntegrationProvider.AGUAS)
                    .plantId(plantEntity.getId())
                    .status(IntegrationHealthStatus.OK)
                    .lastSuccessAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            integrationStatusRepo.upsertStatus(IntegrationStatus.builder()
                    .provider(IntegrationProvider.AGUAS)
                    .plantId(plantEntity.getId())
                    .status(IntegrationHealthStatus.DOWN)
                    .lastErrorAt(LocalDateTime.now())
                    .lastErrorMessage(e.getMessage())
                    .updatedAt(LocalDateTime.now())
                    .build());
            throw e;
        }
        ExpectedAmount saved = expectedRepository.saveAsCurrentVersion(fetched);

        if (previous.isPresent()) {
            auditService.log(AuditAction.EXPECTED_UPDATED, "ExpectedAmount",
                    String.valueOf(saved.getId()),
                    "v" + previous.get().getVersion() + " total=" + previous.get().getExpectedTotal(),
                    "v" + saved.getVersion() + " total=" + saved.getExpectedTotal());
        } else {
            auditService.log(AuditAction.EXPECTED_FETCHED, "ExpectedAmount",
                    String.valueOf(saved.getId()), null,
                    "reparto=" + routeNumber + " fecha=" + date + " total=" + saved.getExpectedTotal()
                            + " efectivo=" + saved.getExpectedCash()
                            + " cheques=" + saved.getExpectedChecks()
                            + " retenciones=" + saved.getExpectedWithholdings());
        }

        return saved;
    }
}
