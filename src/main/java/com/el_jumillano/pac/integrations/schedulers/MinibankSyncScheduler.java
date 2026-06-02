package com.el_jumillano.pac.integrations.schedulers;

import com.el_jumillano.pac.cashiers.infrastructure.CashierJpaRepository;
import com.el_jumillano.pac.deposits.application.SyncMinibankDepositsUseCase;
import com.el_jumillano.pac.shared.exception.IntegrationUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinibankSyncScheduler {

    private final SyncMinibankDepositsUseCase syncUseCase;
    private final CashierJpaRepository cashierRepo;

    @Scheduled(fixedDelayString = "${pac.scheduler.minibank.fixed-delay-ms:300000}")
    public void sync() {
        LocalDate today = LocalDate.now();
        cashierRepo.findAll().forEach(cashier -> {
            try {
                syncUseCase.execute(today, cashier.getExternalCashierNumber());
            } catch (IntegrationUnavailableException e) {
                log.error("Minibank no disponible para cajero {}: {}",
                        cashier.getExternalCashierNumber(), e.getMessage());
            } catch (Exception e) {
                log.error("Error sincronizando Minibank cajero {}: {}",
                        cashier.getExternalCashierNumber(), e.getMessage());
            }
        });
    }
}
