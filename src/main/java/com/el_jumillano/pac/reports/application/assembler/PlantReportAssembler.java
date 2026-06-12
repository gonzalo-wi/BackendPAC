package com.el_jumillano.pac.reports.application.assembler;

import com.el_jumillano.pac.plants.domain.Plant;
import com.el_jumillano.pac.plants.infrastructure.PlantRepositoryAdapter;
import com.el_jumillano.pac.reconciliation.domain.Reconciliation;
import com.el_jumillano.pac.reconciliation.infrastructure.ReconciliationRepositoryAdapter;
import com.el_jumillano.pac.reports.domain.PlantReportSection;
import com.el_jumillano.pac.reports.domain.RouteReportLine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class PlantReportAssembler {

    private final ReconciliationRepositoryAdapter reconciliationRepository;
    private final PlantRepositoryAdapter plantRepository;

    public PlantReportSection assemble(Long plantId, LocalDate date) {
        Plant plant = plantRepository.findById(plantId);
        List<Reconciliation> reconciliations =
                reconciliationRepository.findByPlantAndDate(plantId, date);

        List<RouteReportLine> lines = reconciliations.stream()
                .map(this::toLine)
                .toList();

        return buildSection(plant, lines);
    }

    private RouteReportLine toLine(Reconciliation rec) {
        return new RouteReportLine(
                rec.getRouteNumber(),
                orZero(rec.getMinibankCashTotal()),
                orZero(rec.getManualChecksTotal()),
                orZero(rec.getManualWithholdingsTotal()),
                orZero(rec.getTotalReceived()),
                orZero(rec.getAguasExpectedTotal()),
                orZero(rec.getDifferenceAmount()),
                rec.getDifferenceType(),
                rec.getStatus()
        );
    }

    private PlantReportSection buildSection(Plant plant, List<RouteReportLine> lines) {
        return new PlantReportSection(
                plant.getId(),
                plant.getName(),
                lines,
                sum(lines, RouteReportLine::minibankCash),
                sum(lines, RouteReportLine::checks),
                sum(lines, RouteReportLine::retentions),
                sum(lines, RouteReportLine::totalReceived),
                sum(lines, RouteReportLine::totalExpected),
                sum(lines, RouteReportLine::difference)
        );
    }

    private BigDecimal sum(List<RouteReportLine> lines, Function<RouteReportLine, BigDecimal> getter) {
        return lines.stream().map(getter).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal orZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
