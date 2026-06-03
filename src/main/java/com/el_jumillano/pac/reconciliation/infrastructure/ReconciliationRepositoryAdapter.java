package com.el_jumillano.pac.reconciliation.infrastructure;

import com.el_jumillano.pac.reconciliation.domain.ManualValues;
import com.el_jumillano.pac.reconciliation.domain.Reconciliation;
import com.el_jumillano.pac.reconciliation.domain.ReconciliationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReconciliationRepositoryAdapter {

    private final ReconciliationJpaRepository reconciliationJpaRepository;
    private final ManualValuesJpaRepository manualValuesJpaRepository;
    private final ReconciliationMapper mapper;

    public Reconciliation save(Reconciliation reconciliation) {
        return mapper.toDomain(reconciliationJpaRepository.save(mapper.toEntity(reconciliation)));
    }

    public Optional<Reconciliation> findById(Long id) {
        return reconciliationJpaRepository.findById(id).map(mapper::toDomain);
    }

    public Optional<Reconciliation> findByRouteAndPlantAndDate(Integer routeNumber, Long plantId, LocalDate date) {
        return reconciliationJpaRepository
                .findByRouteNumberAndPlantIdAndDate(routeNumber, plantId, date)
                .map(mapper::toDomain);
    }

    public List<Reconciliation> findByPlantAndDate(Long plantId, LocalDate date) {
        return reconciliationJpaRepository.findByPlantIdAndDateOrderByRouteNumberAsc(plantId, date)
                .stream().map(mapper::toDomain).toList();
    }

    public List<Reconciliation> findByStatuses(List<ReconciliationStatus> statuses) {
        return reconciliationJpaRepository.findByStatusIn(statuses)
                .stream().map(mapper::toDomain).toList();
    }

    public ManualValues saveManualValues(ManualValues manualValues) {
        return mapper.toManualValuesDomain(
                manualValuesJpaRepository.save(mapper.toManualValuesEntity(manualValues)));
    }

    public Optional<ManualValues> findManualValues(Integer routeNumber, Long plantId, LocalDate date) {
        return manualValuesJpaRepository
                .findByRouteNumberAndPlantIdAndDate(routeNumber, plantId, date)
                .map(mapper::toManualValuesDomain);
    }
}
