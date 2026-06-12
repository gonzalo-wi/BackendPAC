package com.el_jumillano.pac.reports.application.port;

import com.el_jumillano.pac.reports.domain.ConsolidatedReportData;
import com.el_jumillano.pac.reports.domain.PlantReportSection;

import java.time.LocalDate;
import java.util.List;

public interface PdfReportPort {

    byte[] generatePlantDepositReport(List<PlantReportSection> sections, LocalDate date);

    byte[] generateConsolidatedReport(ConsolidatedReportData data);
}
