package com.el_jumillano.pac.plants.application;

import com.el_jumillano.pac.cashiers.infrastructure.CashierRepositoryAdapter;
import com.el_jumillano.pac.deposits.application.SyncMinibankDepositsUseCase;
import com.el_jumillano.pac.expected.domain.ExpectedAmount;
import com.el_jumillano.pac.expected.infrastructure.ExpectedAmountRepositoryAdapter;
import com.el_jumillano.pac.integrations.aguas.AguasClient;
import com.el_jumillano.pac.plants.infrastructure.PlantRepositoryAdapter;
import com.el_jumillano.pac.reconciliation.application.CreateReconciliationUseCase;
import com.el_jumillano.pac.reconciliation.application.ProcessReconciliationUseCase;
import com.el_jumillano.pac.reconciliation.domain.Reconciliation;
import com.el_jumillano.pac.reconciliation.domain.ReconciliationStatus;
import com.el_jumillano.pac.reconciliation.infrastructure.ReconciliationRepositoryAdapter;
import com.el_jumillano.pac.shared.exception.IntegrationUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RefreshPlantUseCase {

    private static final List<ReconciliationStatus> FROZEN = List.of(ReconciliationStatus.CLOSED);

    private final PlantRepositoryAdapter plantRepository;
    private final CashierRepositoryAdapter cashierRepository;
    private final SyncMinibankDepositsUseCase syncMinibank;
    private final AguasClient aguasClient;
    private final ExpectedAmountRepositoryAdapter expectedRepository;
    private final ReconciliationRepositoryAdapter reconciliationRepository;
    private final AutoPopulateCheckItemsUseCase autoPopulate;
    private final CreateReconciliationUseCase createReconciliation;
    private final ProcessReconciliationUseCase processReconciliation;
    private final Executor ioTaskExecutor;

    public RefreshPlantUseCase(
            PlantRepositoryAdapter plantRepository,
            CashierRepositoryAdapter cashierRepository,
            SyncMinibankDepositsUseCase syncMinibank,
            AguasClient aguasClient,
            ExpectedAmountRepositoryAdapter expectedRepository,
            ReconciliationRepositoryAdapter reconciliationRepository,
            AutoPopulateCheckItemsUseCase autoPopulate,
            CreateReconciliationUseCase createReconciliation,
            ProcessReconciliationUseCase processReconciliation,
            @Qualifier("ioTaskExecutor") Executor ioTaskExecutor) {
        this.plantRepository = plantRepository;
        this.cashierRepository = cashierRepository;
        this.syncMinibank = syncMinibank;
        this.aguasClient = aguasClient;
        this.expectedRepository = expectedRepository;
        this.reconciliationRepository = reconciliationRepository;
        this.autoPopulate = autoPopulate;
        this.createReconciliation = createReconciliation;
        this.processReconciliation = processReconciliation;
        this.ioTaskExecutor = ioTaskExecutor;
    }

    public List<Reconciliation> execute(Long plantId, LocalDate date, String userId) {
        plantRepository.findById(plantId); // valida existencia, lanza EntityNotFoundException si no existe

        // 1. Sync Minibank — todos los cajeros activos de la planta en paralelo
        var minibankFutures = cashierRepository.findActiveByPlantId(plantId).stream()
                .map(cashier -> CompletableFuture.runAsync(() -> {
                    try {
                        syncMinibank.execute(date, cashier.getExternalCashierNumber());
                    } catch (Exception e) {
                        log.warn("[Refresh] Minibank cajero {} falló: {}", cashier.getExternalCashierNumber(), e.getMessage());
                    }
                }, ioTaskExecutor))
                .toList();
        CompletableFuture.allOf(minibankFutures.toArray(new CompletableFuture[0])).join();

        // 2. Sync Aguas — una sola llamada trae todos los repartos de la fecha
        List<ExpectedAmount> plantExpected = List.of();
        try {
            plantExpected = aguasClient.getAllExpectedByDate(date).stream()
                    .filter(ea -> plantId.equals(ea.getPlantId()))
                    .toList();

            var createFutures = plantExpected.stream()
                    .filter(ea -> isNotFrozen(ea.getRouteNumber(), plantId, date))
                    .map(ea -> CompletableFuture.runAsync(() -> {
                        try {
                            expectedRepository.saveAsCurrentVersion(ea);
                            createReconciliation.execute(ea.getRouteNumber(), date, userId);
                        } catch (Exception e) {
                            log.warn("[Refresh] Error en reparto {}: {}", ea.getRouteNumber(), e.getMessage());
                        }
                    }, ioTaskExecutor))
                    .toList();
            CompletableFuture.allOf(createFutures.toArray(new CompletableFuture[0])).join();

        } catch (IntegrationUnavailableException e) {
            log.error("[Refresh] Aguas no disponible para planta {} fecha {}: {}", plantId, date, e.getMessage());
        }

        // 3. Pre-cargar cheques/retenciones en paralelo para repartos que los tienen
        var itemFutures = plantExpected.stream()
                .filter(ea -> hasCheckOrWithholding(ea))
                .filter(ea -> isNotFrozen(ea.getRouteNumber(), plantId, date))
                .map(ea -> CompletableFuture.runAsync(() -> {
                    try {
                        autoPopulate.execute(ea.getRouteNumber(), plantId, date);
                    } catch (Exception e) {
                        log.warn("[Refresh] Error auto-populando reparto {}: {}", ea.getRouteNumber(), e.getMessage());
                    }
                }, ioTaskExecutor))
                .toList();
        CompletableFuture.allOf(itemFutures.toArray(new CompletableFuture[0])).join();

        // 4. Procesar en paralelo usando los esperados ya cargados (sin re-llamar a Aguas)
        Map<Integer, ExpectedAmount> expectedByRoute = plantExpected.stream()
                .collect(Collectors.toMap(ExpectedAmount::getRouteNumber, ea -> ea));

        var processFutures = reconciliationRepository.findByPlantAndDate(plantId, date).stream()
                .filter(rec -> !FROZEN.contains(rec.getStatus()))
                .map(rec -> CompletableFuture.runAsync(() -> {
                    try {
                        ExpectedAmount preloaded = expectedByRoute.get(rec.getRouteNumber());
                        if (preloaded != null) {
                            processReconciliation.executeWithExpected(rec.getId(), preloaded, userId);
                        } else {
                            processReconciliation.execute(rec.getId(), userId);
                        }
                    } catch (Exception e) {
                        log.warn("[Refresh] Error procesando reparto {}: {}", rec.getRouteNumber(), e.getMessage());
                    }
                }, ioTaskExecutor))
                .toList();
        CompletableFuture.allOf(processFutures.toArray(new CompletableFuture[0])).join();

        return reconciliationRepository.findByPlantAndDate(plantId, date);
    }

    private boolean isNotFrozen(Integer routeNumber, Long plantId, LocalDate date) {
        return reconciliationRepository.findByRouteAndPlantAndDate(routeNumber, plantId, date)
                .map(rec -> !FROZEN.contains(rec.getStatus()))
                .orElse(true);
    }

    private boolean hasCheckOrWithholding(ExpectedAmount ea) {
        return (ea.getExpectedChecks() != null && ea.getExpectedChecks().compareTo(BigDecimal.ZERO) > 0)
                || (ea.getExpectedWithholdings() != null && ea.getExpectedWithholdings().compareTo(BigDecimal.ZERO) > 0);
    }
}
