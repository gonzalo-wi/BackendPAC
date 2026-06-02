package com.el_jumillano.pac.deposits.application;

import com.el_jumillano.pac.audit.application.AuditService;
import com.el_jumillano.pac.audit.domain.AuditAction;
import com.el_jumillano.pac.cashiers.infrastructure.CashierJpaEntity;
import com.el_jumillano.pac.cashiers.infrastructure.CashierJpaRepository;
import com.el_jumillano.pac.deposits.domain.Deposit;
import com.el_jumillano.pac.deposits.infrastructure.DepositRepositoryAdapter;
import com.el_jumillano.pac.integrations.health.IntegrationHealthStatus;
import com.el_jumillano.pac.integrations.health.IntegrationProvider;
import com.el_jumillano.pac.integrations.health.IntegrationStatus;
import com.el_jumillano.pac.integrations.health.IntegrationStatusRepositoryAdapter;
import com.el_jumillano.pac.integrations.minibank.MinibankClient;
import com.el_jumillano.pac.shared.exception.EntityNotFoundException;
import com.el_jumillano.pac.shared.exception.IntegrationUnavailableException;
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
public class SyncMinibankDepositsUseCase {

    private final MinibankClient minibankClient;
    private final DepositRepositoryAdapter depositRepository;
    private final CashierJpaRepository cashierRepo;
    private final IntegrationStatusRepositoryAdapter integrationStatusRepo;
    private final AuditService auditService;

    @Transactional
    public List<Deposit> execute(LocalDate date, Integer externalCashierId) {
        CashierJpaEntity cashier = cashierRepo.findByExternalCashierNumber(externalCashierId)
                .orElseThrow(() -> new EntityNotFoundException("Cashier", externalCashierId));

        List<Deposit> deposits;
        try {
            deposits = minibankClient.getDepositsByDate(date, externalCashierId);
            integrationStatusRepo.upsertStatus(IntegrationStatus.builder()
                    .provider(IntegrationProvider.MINIBANK)
                    .plantId(cashier.getPlantId())
                    .cashierId(cashier.getId())
                    .status(IntegrationHealthStatus.OK)
                    .lastSuccessAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());
        } catch (IntegrationUnavailableException e) {
            log.error("Minibank no disponible para cajero {} fecha {}: {}", externalCashierId, date, e.getMessage());
            auditService.log(AuditAction.MINIBANK_ERROR, "Deposit", "cajero=" + externalCashierId,
                    null, "fecha=" + date + " error=" + e.getMessage());
            integrationStatusRepo.upsertStatus(IntegrationStatus.builder()
                    .provider(IntegrationProvider.MINIBANK)
                    .plantId(cashier.getPlantId())
                    .cashierId(cashier.getId())
                    .status(IntegrationHealthStatus.DOWN)
                    .lastErrorAt(LocalDateTime.now())
                    .lastErrorMessage(e.getMessage())
                    .updatedAt(LocalDateTime.now())
                    .build());
            throw e;
        }

        List<Deposit> saved = deposits.stream()
                .map(depositRepository::save)
                .toList();

        // Solo auditar depósitos nuevos (los que tenían id=null antes de persistir)
        long newCount = deposits.stream()
                .filter(d -> d.getId() == null)
                .count();

        saved.stream()
                .filter(d -> deposits.stream().anyMatch(
                        orig -> orig.getId() == null
                                && orig.getExternalDepositId().equals(d.getExternalDepositId())))
                .forEach(d -> auditService.log(
                        AuditAction.DEPOSIT_DETECTED, "Deposit", String.valueOf(d.getId()), null,
                        "externalId=" + d.getExternalDepositId()
                                + " reparto=" + d.getRouteNumber()
                                + " monto=" + d.getAmount()
                                + " fecha=" + d.getDepositDate()));

        log.info("Sincronizados {} depósitos para cajero {} en fecha {} ({} nuevos, {} ya existían)",
                saved.size(), externalCashierId, date, newCount, saved.size() - newCount);
        return saved;
    }
}
