package com.el_jumillano.pac.reconciliation.infrastructure.messaging;

import com.el_jumillano.pac.audit.application.AuditService;
import com.el_jumillano.pac.audit.domain.AuditAction;
import com.el_jumillano.pac.deposits.infrastructure.CheckJpaRepository;
import com.el_jumillano.pac.deposits.infrastructure.WithholdingJpaRepository;
import com.el_jumillano.pac.integrations.aguas.AguasClient;
import com.el_jumillano.pac.integrations.aguas.AguasCloseItemBuilder;
import com.el_jumillano.pac.reconciliation.domain.CloseRouteResult;
import com.el_jumillano.pac.reconciliation.domain.Reconciliation;
import com.el_jumillano.pac.reconciliation.domain.ReconciliationStatus;
import com.el_jumillano.pac.reconciliation.infrastructure.ReconciliationRepositoryAdapter;
import com.el_jumillano.pac.shared.config.RabbitMQConfig;
import com.el_jumillano.pac.shared.exception.EntityNotFoundException;
import com.el_jumillano.pac.shared.exception.IntegrationUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RouteCloseConsumer {

    private final ReconciliationRepositoryAdapter reconciliationRepository;
    private final AguasClient aguasClient;
    private final AuditService auditService;
    private final CheckJpaRepository checkRepository;
    private final WithholdingJpaRepository withholdingRepository;
    private final AguasCloseItemBuilder itemBuilder;

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.QUEUE_CLOSE)
    public void onMessage(RouteCloseMessage message) {
        log.info("Procesando cierre de reparto {} para reconciliación {}",
                message.getRouteNumber(), message.getReconciliationId());

        Reconciliation rec = reconciliationRepository.findById(message.getReconciliationId())
                .orElseThrow(() -> new EntityNotFoundException("Reconciliation", message.getReconciliationId()));

        var checks       = checkRepository.findByRouteAndPlantAndDate(
                rec.getRouteNumber(), rec.getPlantId(), rec.getDate());
        var withholdings = withholdingRepository.findByRouteAndPlantAndDate(
                rec.getRouteNumber(), rec.getPlantId(), rec.getDate());

        String checksJson       = itemBuilder.buildChecks(checks);
        String withholdingsJson = itemBuilder.buildWithholdings(withholdings);

        log.debug("Reparto {} - cheques: {} - retenciones: {}",
                rec.getRouteNumber(), checksJson, withholdingsJson);

        CloseRouteResult result;
        try {
            result = aguasClient.closeRouteWithExpectedAmount(
                    rec.getDate(),
                    rec.getRouteNumber(),
                    rec.getAguasExpectedCash(),
                    checksJson,
                    withholdingsJson,
                    message.getUserId()
            );
        } catch (IntegrationUnavailableException e) {
            log.error("Aguas no disponible al cerrar reparto {}: {}", rec.getRouteNumber(), e.getMessage());
            rec.setStatus(ReconciliationStatus.INTEGRATION_ERROR);
            reconciliationRepository.save(rec);
            auditService.log(AuditAction.AGUAS_ERROR, "Reconciliation",
                    String.valueOf(rec.getId()), null, e.getMessage(), message.getUserId());
            return;
        }

        rec.setAguasResponse(result.getRawResponse());

        if (result.isSuccess()) {
            rec.setClosedAt(LocalDateTime.now());
            rec.setStatus(ReconciliationStatus.CLOSED);
            auditService.log(AuditAction.ROUTE_CLOSED, "Reconciliation",
                    String.valueOf(rec.getId()), null,
                    "reparto=" + rec.getRouteNumber()
                            + " efectivo=" + rec.getAguasExpectedCash()
                            + " cheques=" + checks.size()
                            + " retenciones=" + withholdings.size(),
                    message.getUserId());
        } else {
            log.warn("Aguas rechazó el cierre del reparto {}: {}", rec.getRouteNumber(), result.getErrorMessage());
            rec.setStatus(ReconciliationStatus.INTEGRATION_ERROR);
            auditService.log(AuditAction.AGUAS_ERROR, "Reconciliation",
                    String.valueOf(rec.getId()), null,
                    "Aguas rechazó el cierre: " + result.getErrorMessage(), message.getUserId());
        }

        reconciliationRepository.save(rec);
    }
}
