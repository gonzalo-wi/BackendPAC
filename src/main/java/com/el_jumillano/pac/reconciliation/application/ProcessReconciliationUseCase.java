package com.el_jumillano.pac.reconciliation.application;

import com.el_jumillano.pac.audit.application.AuditService;
import com.el_jumillano.pac.audit.domain.AuditAction;
import com.el_jumillano.pac.differences.application.DifferencePolicy;
import com.el_jumillano.pac.differences.domain.DifferenceRecord;
import com.el_jumillano.pac.differences.domain.DifferenceType;
import com.el_jumillano.pac.differences.infrastructure.DifferenceRecordRepositoryAdapter;
import com.el_jumillano.pac.deposits.infrastructure.DepositItemRepositoryAdapter;
import com.el_jumillano.pac.deposits.infrastructure.DepositRepositoryAdapter;
import com.el_jumillano.pac.expected.domain.ExpectedAmount;
import com.el_jumillano.pac.integrations.aguas.AguasClient;
import com.el_jumillano.pac.integrations.health.DifferenceClient;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessReconciliationUseCase {

    private static final List<ReconciliationStatus> NON_REPROCESSABLE = List.of(
            ReconciliationStatus.CLOSED
    );

    private final ReconciliationRepositoryAdapter reconciliationRepository;
    private final DepositRepositoryAdapter depositRepository;
    private final DepositItemRepositoryAdapter depositItemRepository;
    private final DifferenceRecordRepositoryAdapter differenceRepository;
    private final AguasClient aguasClient;
    private final DifferenceClient differenceClient;
    private final DifferencePolicy differencePolicy;
    private final AuditService auditService;

    /** Uso normal: refresca el esperado desde Aguas antes de calcular. */
    @Transactional
    public Reconciliation execute(Long reconciliationId, String userId) {
        Reconciliation rec = findAndValidate(reconciliationId);
        ExpectedAmount expected = tryRefreshExpected(rec);
        if (expected == null) {
            rec.setStatus(ReconciliationStatus.INTEGRATION_ERROR);
            return reconciliationRepository.save(rec);
        }
        return calculate(rec, expected, userId);
    }

    /**
     * Uso durante el refresh masivo: recibe el esperado ya pre-cargado
     * para evitar N llamadas individuales a Aguas.
     */
    @Transactional
    public Reconciliation executeWithExpected(Long reconciliationId, ExpectedAmount preloaded, String userId) {
        Reconciliation rec = findAndValidate(reconciliationId);
        return calculate(rec, preloaded, userId);
    }

    private Reconciliation findAndValidate(Long reconciliationId) {
        Reconciliation rec = reconciliationRepository.findById(reconciliationId)
                .orElseThrow(() -> new EntityNotFoundException("Reconciliation", reconciliationId));
        if (NON_REPROCESSABLE.contains(rec.getStatus())) {
            throw new ReconciliationAlreadyProcessedException(reconciliationId);
        }
        return rec;
    }

    private Reconciliation calculate(Reconciliation rec, ExpectedAmount expected, String userId) {
        Long reconciliationId = rec.getId();

        BigDecimal minibankTotal = depositRepository.sumByRouteAndPlantAndDate(
                rec.getRouteNumber(), rec.getPlantId(), rec.getDate());
        if (minibankTotal == null) minibankTotal = BigDecimal.ZERO;

        BigDecimal checks       = depositItemRepository.sumChecksByRouteAndPlantAndDate(
                rec.getRouteNumber(), rec.getPlantId(), rec.getDate());
        BigDecimal withholdings = depositItemRepository.sumWithholdingsByRouteAndPlantAndDate(
                rec.getRouteNumber(), rec.getPlantId(), rec.getDate());

        BigDecimal totalReceived    = minibankTotal.add(checks).add(withholdings);
        BigDecimal differenceAmount = totalReceived.subtract(expected.getExpectedTotal());
        DifferenceType differenceType = differencePolicy.classify(differenceAmount);

        rec.setMinibankCashTotal(minibankTotal);
        rec.setManualChecksTotal(checks);
        rec.setManualWithholdingsTotal(withholdings);
        rec.setTotalReceived(totalReceived);
        rec.setAguasExpectedCash(expected.getExpectedCash());
        rec.setAguasExpectedChecks(expected.getExpectedChecks());
        rec.setAguasExpectedWithholdings(expected.getExpectedWithholdings());
        rec.setAguasExpectedTotal(expected.getExpectedTotal());
        rec.setDifferenceAmount(differenceAmount);
        rec.setDifferenceType(differenceType);
        rec.setProcessedBy(userId);
        rec.setProcessedAt(LocalDateTime.now());

        if (differenceType != DifferenceType.NONE) {
            DifferenceRecord diffRecord = DifferenceRecord.builder()
                    .reconciliationId(rec.getId())
                    .routeNumber(rec.getRouteNumber())
                    .plantId(rec.getPlantId())
                    .date(rec.getDate())
                    .amount(differenceAmount)
                    .type(differenceType)
                    .notified(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            DifferenceRecord saved = differenceRepository.save(diffRecord);
            try {
                differenceClient.notifyDifference(saved);
            } catch (Exception e) {
                log.warn("Error notificando diferencia: {}", e.getMessage());
            }
            auditService.log(AuditAction.DIFFERENCE_DETECTED, "Reconciliation",
                    String.valueOf(reconciliationId), null,
                    "tipo=" + differenceType + " monto=" + differenceAmount, userId);
        }

        // Si Aguas espera cheques o retenciones pero el cajero aún no los cargó → Pendiente
        boolean pendingChecks = expected.getExpectedChecks() != null
                && expected.getExpectedChecks().compareTo(BigDecimal.ZERO) > 0
                && checks.compareTo(BigDecimal.ZERO) == 0;
        boolean pendingWithholdings = expected.getExpectedWithholdings() != null
                && expected.getExpectedWithholdings().compareTo(BigDecimal.ZERO) > 0
                && withholdings.compareTo(BigDecimal.ZERO) == 0;

        if (pendingChecks || pendingWithholdings) {
            rec.setStatus(ReconciliationStatus.AWAITING_MANUAL_ITEMS);
        } else {
            rec.setStatus(switch (differenceType) {
                case SURPLUS_NORMAL, SURPLUS_HIGH -> ReconciliationStatus.PROCESSED_WITH_SURPLUS;
                case SHORTAGE, SHORTAGE_CRITICAL  -> ReconciliationStatus.PROCESSED_WITH_SHORTAGE;
                case NONE                         -> ReconciliationStatus.PROCESSED_WITHOUT_DIFFERENCE;
            });
        }

        Reconciliation finalRec = reconciliationRepository.save(rec);

        auditService.log(AuditAction.RECONCILIATION_CALCULATED, "Reconciliation",
                String.valueOf(reconciliationId), null,
                "minibank=" + minibankTotal + " cheques=" + checks + " retenciones=" + withholdings
                        + " totalRecibido=" + totalReceived + " esperadoAguas=" + expected.getExpectedTotal(),
                userId);
        auditService.log(AuditAction.ROUTE_PROCESSED, "Reconciliation",
                String.valueOf(reconciliationId), null,
                "status=" + finalRec.getStatus() + " diferencia=" + differenceAmount, userId);

        return finalRec;
    }

    private ExpectedAmount tryRefreshExpected(Reconciliation rec) {
        try {
            return aguasClient.getExpectedByRoute(rec.getDate(), rec.getRouteNumber());
        } catch (IntegrationUnavailableException e) {
            log.error("Aguas no disponible al refrescar esperado: {}", e.getMessage());
            auditService.log(AuditAction.AGUAS_ERROR, "Reconciliation",
                    String.valueOf(rec.getId()), null, e.getMessage());
            return null;
        }
    }
}
