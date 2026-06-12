package com.el_jumillano.pac.reports.application.usecase;

import com.el_jumillano.pac.plants.infrastructure.PlantRepositoryAdapter;
import com.el_jumillano.pac.reports.application.assembler.PlantReportAssembler;
import com.el_jumillano.pac.reports.application.port.PdfReportPort;
import com.el_jumillano.pac.reports.domain.PlantReportSection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneratePlantDepositReportUseCase {

    private final PlantRepositoryAdapter plantRepository;
    private final PlantReportAssembler assembler;
    private final PdfReportPort pdfReportPort;

    @Transactional(readOnly = true)
    public byte[] execute(LocalDate date) {
        List<PlantReportSection> sections = plantRepository.findAll().stream()
                .map(plant -> assembler.assemble(plant.getId(), date))
                .toList();
        return pdfReportPort.generatePlantDepositReport(sections, date);
    }

    @Transactional(readOnly = true)
    public byte[] executeForPlant(Long plantId, LocalDate date) {
        PlantReportSection section = assembler.assemble(plantId, date);
        return pdfReportPort.generatePlantDepositReport(List.of(section), date);
    }
}
