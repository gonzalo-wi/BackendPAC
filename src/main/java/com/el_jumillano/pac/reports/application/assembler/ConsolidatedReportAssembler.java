package com.el_jumillano.pac.reports.application.assembler;

import com.el_jumillano.pac.plants.infrastructure.PlantRepositoryAdapter;
import com.el_jumillano.pac.reports.domain.ConsolidatedReportData;
import com.el_jumillano.pac.reports.domain.PlantReportSection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ConsolidatedReportAssembler {

    private final PlantRepositoryAdapter plantRepository;
    private final PlantReportAssembler plantReportAssembler;

    public ConsolidatedReportData assemble(LocalDate date) {
        List<PlantReportSection> sections = plantRepository.findAll().stream()
                .map(plant -> plantReportAssembler.assemble(plant.getId(), date))
                .toList();

        return new ConsolidatedReportData(
                date,
                sections,
                sum(sections, PlantReportSection::subtotalCash),
                sum(sections, PlantReportSection::subtotalChecks),
                sum(sections, PlantReportSection::subtotalRetentions),
                sum(sections, PlantReportSection::subtotalReceived),
                sum(sections, PlantReportSection::subtotalExpected),
                sum(sections, PlantReportSection::subtotalDifference)
        );
    }

    private BigDecimal sum(List<PlantReportSection> sections, Function<PlantReportSection, BigDecimal> getter) {
        return sections.stream().map(getter).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
