package com.el_jumillano.pac.reports.infrastructure.pdf;

import com.el_jumillano.pac.reports.application.port.PdfReportPort;
import com.el_jumillano.pac.reports.domain.ConsolidatedReportData;
import com.el_jumillano.pac.reports.domain.PlantReportSection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ITextPdfReportAdapter implements PdfReportPort {

    private final PlantDepositPdfRenderer plantDepositRenderer;
    private final ConsolidatedPdfRenderer consolidatedRenderer;

    @Override
    public byte[] generatePlantDepositReport(List<PlantReportSection> sections, LocalDate date) {
        return plantDepositRenderer.render(sections, date);
    }

    @Override
    public byte[] generateConsolidatedReport(ConsolidatedReportData data) {
        return consolidatedRenderer.render(data);
    }
}
