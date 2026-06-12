package com.el_jumillano.pac.reports.application.usecase;

import com.el_jumillano.pac.reports.application.assembler.ConsolidatedReportAssembler;
import com.el_jumillano.pac.reports.application.port.PdfReportPort;
import com.el_jumillano.pac.reports.domain.ConsolidatedReportData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GenerateConsolidatedReportUseCase {

    private final ConsolidatedReportAssembler assembler;
    private final PdfReportPort pdfReportPort;

    @Transactional(readOnly = true)
    public byte[] execute(LocalDate date) {
        ConsolidatedReportData data = assembler.assemble(date);
        return pdfReportPort.generateConsolidatedReport(data);
    }
}
