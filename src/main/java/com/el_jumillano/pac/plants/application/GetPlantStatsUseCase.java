package com.el_jumillano.pac.plants.application;

import com.el_jumillano.pac.differences.domain.DifferenceType;
import com.el_jumillano.pac.plants.domain.Plant;
import com.el_jumillano.pac.plants.infrastructure.PlantRepositoryAdapter;
import com.el_jumillano.pac.reconciliation.domain.Reconciliation;
import com.el_jumillano.pac.reconciliation.domain.ReconciliationStatus;
import com.el_jumillano.pac.reconciliation.infrastructure.ReconciliationRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class GetPlantStatsUseCase {

    private static final Set<ReconciliationStatus> PROCESSED_STATUSES = Set.of(
            ReconciliationStatus.PROCESSED_WITH_SURPLUS,
            ReconciliationStatus.PROCESSED_WITH_SHORTAGE,
            ReconciliationStatus.PROCESSED_WITHOUT_DIFFERENCE
    );

    private static final Set<ReconciliationStatus> PENDING_STATUSES = Set.of(
            ReconciliationStatus.PENDING,
            ReconciliationStatus.READY_TO_PROCESS,
            ReconciliationStatus.REQUIRES_REVIEW
    );

    private static final Set<DifferenceType> SURPLUS_TYPES = Set.of(
            DifferenceType.SURPLUS_NORMAL,
            DifferenceType.SURPLUS_HIGH
    );

    private static final Set<DifferenceType> SHORTAGE_TYPES = Set.of(
            DifferenceType.SHORTAGE,
            DifferenceType.SHORTAGE_CRITICAL
    );

    private final PlantRepositoryAdapter plantRepository;
    private final ReconciliationRepositoryAdapter reconciliationRepository;

    public PlantStatsResponse getByPlant(Long plantId, LocalDate date) {
        Plant plant = plantRepository.findById(plantId);
        List<Reconciliation> recs = reconciliationRepository.findByPlantAndDate(plantId, date);
        return compute(plant, date, recs);
    }

    public List<PlantStatsResponse> getAllPlants(LocalDate date) {
        return plantRepository.findAll().stream()
                .map(plant -> {
                    List<Reconciliation> recs = reconciliationRepository.findByPlantAndDate(plant.getId(), date);
                    return compute(plant, date, recs);
                })
                .toList();
    }

    private PlantStatsResponse compute(Plant plant, LocalDate date, List<Reconciliation> recs) {
        int total     = recs.size();
        int closed    = count(recs, r -> r.getStatus() == ReconciliationStatus.CLOSED);
        int processed = count(recs, r -> PROCESSED_STATUSES.contains(r.getStatus()));
        int awaiting  = count(recs, r -> r.getStatus() == ReconciliationStatus.AWAITING_MANUAL_ITEMS);
        int pending   = count(recs, r -> PENDING_STATUSES.contains(r.getStatus()));
        int errors    = count(recs, r -> r.getStatus() == ReconciliationStatus.INTEGRATION_ERROR);

        double closureRate = total == 0 ? 0.0
                : BigDecimal.valueOf(closed * 100.0 / total).setScale(1, RoundingMode.HALF_UP).doubleValue();

        List<Reconciliation> withAmounts = recs.stream()
                .filter(r -> r.getTotalReceived() != null)
                .toList();

        BigDecimal totalReceived = sum(withAmounts, Reconciliation::getTotalReceived);
        BigDecimal totalExpected = sum(withAmounts, Reconciliation::getAguasExpectedTotal);
        BigDecimal totalDiff     = totalReceived.subtract(totalExpected);

        int surplusRoutes  = count(withAmounts, r -> SURPLUS_TYPES.contains(r.getDifferenceType()));
        int shortageRoutes = count(withAmounts, r -> SHORTAGE_TYPES.contains(r.getDifferenceType()));
        int noDiffRoutes   = count(withAmounts, r -> r.getDifferenceType() == DifferenceType.NONE);

        BigDecimal totalSurplus = withAmounts.stream()
                .filter(r -> r.getDifferenceAmount() != null && r.getDifferenceAmount().compareTo(BigDecimal.ZERO) > 0)
                .map(Reconciliation::getDifferenceAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalShortage = withAmounts.stream()
                .filter(r -> r.getDifferenceAmount() != null && r.getDifferenceAmount().compareTo(BigDecimal.ZERO) < 0)
                .map(r -> r.getDifferenceAmount().abs())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PlantStatsResponse(
                plant.getId(), plant.getName(), plant.getCode(), date,
                total, closed, processed, awaiting, pending, errors, closureRate,
                totalReceived, totalExpected, totalDiff,
                surplusRoutes, shortageRoutes, noDiffRoutes,
                totalSurplus, totalShortage
        );
    }

    private int count(List<Reconciliation> recs, Function<Reconciliation, Boolean> predicate) {
        return (int) recs.stream().filter(predicate::apply).count();
    }

    private BigDecimal sum(List<Reconciliation> recs, Function<Reconciliation, BigDecimal> getter) {
        return recs.stream()
                .map(getter)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
