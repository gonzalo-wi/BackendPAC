package com.el_jumillano.pac.reconciliation.application;

import com.el_jumillano.pac.audit.application.AuditService;
import com.el_jumillano.pac.audit.domain.AuditAction;
import com.el_jumillano.pac.reconciliation.domain.ManualValues;
import com.el_jumillano.pac.reconciliation.domain.Reconciliation;
import com.el_jumillano.pac.reconciliation.domain.ReconciliationStatus;
import com.el_jumillano.pac.reconciliation.infrastructure.ReconciliationRepositoryAdapter;
import com.el_jumillano.pac.shared.exception.EntityNotFoundException;
import com.el_jumillano.pac.shared.exception.ReconciliationAlreadyProcessedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoadManualValuesUseCase {

    private static final List<ReconciliationStatus> IMMUTABLE = List.of(
            ReconciliationStatus.PROCESSED_WITH_SURPLUS,
            ReconciliationStatus.PROCESSED_WITH_SHORTAGE,
            ReconciliationStatus.PROCESSED_WITHOUT_DIFFERENCE,
            ReconciliationStatus.CLOSED
    );

    private final ReconciliationRepositoryAdapter repository;
    private final AuditService auditService;

    @Transactional
    public Reconciliation execute(Long reconciliationId, ManualValuesRequest request, String userId) {
        Reconciliation reconciliation = repository.findById(reconciliationId)
                .orElseThrow(() -> new EntityNotFoundException("Reconciliation", reconciliationId));

        if (IMMUTABLE.contains(reconciliation.getStatus())) {
            throw new ReconciliationAlreadyProcessedException(reconciliationId);
        }

        ManualValues manual = ManualValues.builder()
                .routeNumber(reconciliation.getRouteNumber())
                .plantId(reconciliation.getPlantId())
                .date(reconciliation.getDate())
                .checksAmount(request.checksAmount())
                .withholdingsAmount(request.withholdingsAmount())
                .enteredBy(userId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        repository.saveManualValues(manual);

        reconciliation.setManualChecksTotal(request.checksAmount());
        reconciliation.setManualWithholdingsTotal(request.withholdingsAmount());
        reconciliation.setStatus(ReconciliationStatus.READY_TO_PROCESS);
        Reconciliation saved = repository.save(reconciliation);

        auditService.log(AuditAction.MANUAL_CHECK_LOADED, "Reconciliation",
                String.valueOf(reconciliationId), null,
                "cheques=" + request.checksAmount() + " retenciones=" + request.withholdingsAmount(),
                userId);

        return saved;
    }
}
