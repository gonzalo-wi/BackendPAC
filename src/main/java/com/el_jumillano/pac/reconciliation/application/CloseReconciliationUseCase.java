package com.el_jumillano.pac.reconciliation.application;

import com.el_jumillano.pac.audit.application.AuditService;
import com.el_jumillano.pac.audit.domain.AuditAction;
import com.el_jumillano.pac.reconciliation.domain.Reconciliation;
import com.el_jumillano.pac.reconciliation.domain.ReconciliationStatus;
import com.el_jumillano.pac.reconciliation.infrastructure.ReconciliationRepositoryAdapter;
import com.el_jumillano.pac.reconciliation.infrastructure.messaging.RouteCloseMessage;
import com.el_jumillano.pac.shared.exception.EntityNotFoundException;
import com.el_jumillano.pac.shared.exception.ReconciliationAlreadyProcessedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloseReconciliationUseCase {

    private static final List<ReconciliationStatus> CLOSEABLE = List.of(
            ReconciliationStatus.PROCESSED_WITH_SURPLUS,
            ReconciliationStatus.PROCESSED_WITH_SHORTAGE,
            ReconciliationStatus.PROCESSED_WITHOUT_DIFFERENCE,
            ReconciliationStatus.INTEGRATION_ERROR
    );

    private final ReconciliationRepositoryAdapter reconciliationRepository;
    private final RouteClosePublisherPort routeClosePublisher;
    private final AuditService auditService;

    @Transactional
    public Reconciliation execute(Long reconciliationId, String userId) {
        Reconciliation rec = reconciliationRepository.findById(reconciliationId)
                .orElseThrow(() -> new EntityNotFoundException("Reconciliation", reconciliationId));

        if (rec.getStatus() == ReconciliationStatus.CLOSED
                || rec.getStatus() == ReconciliationStatus.QUEUED_FOR_CLOSE) {
            throw new ReconciliationAlreadyProcessedException(reconciliationId);
        }

        if (!CLOSEABLE.contains(rec.getStatus())) {
            throw new IllegalStateException(
                    "La reconciliación " + reconciliationId + " no está en un estado válido para cerrar (estado actual: " + rec.getStatus() + ")");
        }

        RouteCloseMessage message = RouteCloseMessage.builder()
                .reconciliationId(reconciliationId)
                .routeNumber(rec.getRouteNumber())
                .plantId(rec.getPlantId())
                .date(rec.getDate())
                .expectedAmount(rec.getAguasExpectedTotal())
                .userId(userId)
                .build();

        routeClosePublisher.publish(message);

        rec.setAguasSentAmount(rec.getAguasExpectedTotal());
        rec.setStatus(ReconciliationStatus.QUEUED_FOR_CLOSE);
        Reconciliation saved = reconciliationRepository.save(rec);

        auditService.log(AuditAction.ROUTE_PROCESSED, "Reconciliation",
                String.valueOf(reconciliationId), null,
                "Cierre encolado. reparto=" + rec.getRouteNumber() + " monto=" + rec.getAguasExpectedTotal(),
                userId);

        return saved;
    }
}
