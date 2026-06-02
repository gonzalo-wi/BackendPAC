package com.el_jumillano.pac.integrations.schedulers;

import com.el_jumillano.pac.cashiers.infrastructure.CashierJpaRepository;
import com.el_jumillano.pac.integrations.health.IntegrationHealthStatus;
import com.el_jumillano.pac.integrations.health.IntegrationProvider;
import com.el_jumillano.pac.integrations.health.IntegrationStatus;
import com.el_jumillano.pac.integrations.health.IntegrationStatusRepositoryAdapter;
import com.el_jumillano.pac.integrations.minibank.MinibankProperties;
import com.el_jumillano.pac.integrations.minibank.MinibankXmlClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class IntegrationHealthScheduler {

    private static final DateTimeFormatter MINIBANK_DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final IntegrationStatusRepositoryAdapter statusRepo;
    private final CashierJpaRepository cashierRepo;
    private final MinibankXmlClient xmlClient;
    private final MinibankProperties minibankProperties;

    @Scheduled(fixedDelayString = "${pac.scheduler.health.fixed-delay-ms:120000}")
    public void checkHealth() {
        String testDate = LocalDate.now().minusDays(1).format(MINIBANK_DATE_FMT);

        cashierRepo.findAll().forEach(cashier -> {
            IntegrationHealthStatus health;
            String errorMsg = null;
            LocalDateTime successAt = null;
            LocalDateTime errorAt = null;

            String stIdentifier = minibankProperties.getCashierIdentifiers()
                    .get(cashier.getExternalCashierNumber());

            if (stIdentifier == null) {
                health = IntegrationHealthStatus.UNKNOWN;
                errorMsg = "Sin stIdentifier configurado para cajero " + cashier.getExternalCashierNumber();
                errorAt = LocalDateTime.now();
            } else {
                try {
                    // Llama directamente al Feign client, sin circuit breaker ni retry.
                    // Cualquier respuesta HTTP (incluso 4xx "sin datos") confirma conectividad OK.
                    // Solo 5xx o errores de red implican DOWN.
                    xmlClient.getDepositsXml(stIdentifier, testDate);
                    health = IntegrationHealthStatus.OK;
                    successAt = LocalDateTime.now();
                } catch (FeignException e) {
                    int status = e.status();
                    if (status >= 400 && status < 500) {
                        // 4xx: servidor alcanzable y autenticación válida, solo no hay datos
                        health = IntegrationHealthStatus.OK;
                        successAt = LocalDateTime.now();
                        log.debug("[HealthCheck] PIMS respondió {} para {} — conectividad OK (sin datos)", status, stIdentifier);
                    } else {
                        // 5xx o -1 (sin respuesta)
                        health = IntegrationHealthStatus.DOWN;
                        errorMsg = "HTTP " + status + ": " + e.getMessage();
                        errorAt = LocalDateTime.now();
                        log.warn("[HealthCheck] PIMS DOWN para {}: HTTP {}", stIdentifier, status);
                    }
                } catch (Exception e) {
                    health = IntegrationHealthStatus.DOWN;
                    errorMsg = e.getMessage();
                    errorAt = LocalDateTime.now();
                    log.warn("[HealthCheck] PIMS DOWN para {}: {}", stIdentifier, e.getMessage());
                }
            }

            statusRepo.upsertStatus(IntegrationStatus.builder()
                    .provider(IntegrationProvider.MINIBANK)
                    .plantId(cashier.getPlantId())
                    .cashierId(cashier.getId())
                    .status(health)
                    .lastSuccessAt(successAt)
                    .lastErrorAt(errorAt)
                    .lastErrorMessage(errorMsg)
                    .updatedAt(LocalDateTime.now())
                    .build());
        });
    }
}
