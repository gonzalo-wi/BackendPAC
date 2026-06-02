package com.el_jumillano.pac.reconciliation.application;

import com.el_jumillano.pac.audit.application.AuditService;
import com.el_jumillano.pac.audit.domain.AuditAction;
import com.el_jumillano.pac.integrations.aguas.AguasClient;
import com.el_jumillano.pac.reconciliation.domain.CloseRouteResult;
import com.el_jumillano.pac.reconciliation.domain.Reconciliation;
import com.el_jumillano.pac.reconciliation.domain.ReconciliationStatus;
import com.el_jumillano.pac.reconciliation.infrastructure.ReconciliationRepositoryAdapter;
import com.el_jumillano.pac.shared.exception.EntityNotFoundException;
import com.el_jumillano.pac.shared.exception.IntegrationUnavailableException;
import com.el_jumillano.pac.shared.exception.ReconciliationAlreadyProcessedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final AguasClient aguasClient;
    private final AuditService auditService;

    @Transactional
    public Reconciliation execute(Long reconciliationId, String userId) {
        Reconciliation rec = reconciliationRepository.findById(reconciliationId)
                .orElseThrow(() -> new EntityNotFoundException("Reconciliation", reconciliationId));

        if (rec.getStatus() == ReconciliationStatus.CLOSED) {
            throw new ReconciliationAlreadyProcessedException(reconciliationId);
        }

        if (!CLOSEABLE.contains(rec.getStatus())) {
            throw new IllegalStateException(
                    "La reconciliación " + reconciliationId + " no está en un estado válido para cerrar (estado actual: " + rec.getStatus() + ")");
        }

        CloseRouteResult closeResult;
        try {
            closeResult = aguasClient.closeRouteWithExpectedAmount(
                    rec.getDate(), rec.getRouteNumber(), rec.getAguasExpectedTotal());
        } catch (IntegrationUnavailableException e) {
            log.error("Error cerrando reparto en Aguas: {}", e.getMessage());
            rec.setStatus(ReconciliationStatus.INTEGRATION_ERROR);
            auditService.log(AuditAction.AGUAS_ERROR, "Reconciliation",
                    String.valueOf(reconciliationId), null, e.getMessage(), userId);
            return reconciliationRepository.save(rec);
        }

        rec.setAguasSentAmount(rec.getAguasExpectedTotal());
        rec.setAguasResponse(closeResult.getRawResponse());

        if (closeResult.isSuccess()) {
            rec.setClosedAt(LocalDateTime.now());
            rec.setStatus(ReconciliationStatus.CLOSED);
            auditService.log(AuditAction.ROUTE_CLOSED, "Reconciliation",
                    String.valueOf(reconciliationId), null,
                    "reparto=" + rec.getRouteNumber() + " monto=" + rec.getAguasSentAmount(),
                    userId);
        } else {
            log.warn("Aguas rechazó el cierre del reparto {}: {}", rec.getRouteNumber(), closeResult.getRawResponse());
            rec.setStatus(ReconciliationStatus.INTEGRATION_ERROR);
            auditService.log(AuditAction.AGUAS_ERROR, "Reconciliation",
                    String.valueOf(reconciliationId), null,
                    "Aguas rechazó el cierre: " + closeResult.getRawResponse(),
                    userId);
        }

        return reconciliationRepository.save(rec);
    }
}
