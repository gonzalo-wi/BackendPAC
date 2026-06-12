package com.el_jumillano.pac.reports.infrastructure;

import com.el_jumillano.pac.reports.application.usecase.GenerateConsolidatedReportUseCase;
import com.el_jumillano.pac.reports.application.usecase.GeneratePlantDepositReportUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "Reports", description = "Generación de reportes PDF")
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final GeneratePlantDepositReportUseCase plantDepositUseCase;
    private final GenerateConsolidatedReportUseCase consolidatedUseCase;

    @Operation(summary = "Reporte de depósitos — todas las plantas",
               description = "PDF con el detalle de depósitos, cheques y retenciones por planta, " +
                             "ordenado por número de reparto ascendente.")
    @GetMapping(value = "/deposits", produces = "application/pdf")
    public ResponseEntity<byte[]> allPlantsDepositReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        byte[] pdf = plantDepositUseCase.execute(date);
        return pdfResponse(pdf, "depositos_" + date + ".pdf");
    }

    @Operation(summary = "Reporte de depósitos — planta específica",
               description = "PDF con el detalle de una sola planta, ordenado por número de reparto ascendente.")
    @GetMapping(value = "/deposits/plant/{plantId}", produces = "application/pdf")
    public ResponseEntity<byte[]> singlePlantDepositReport(
            @PathVariable Long plantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        byte[] pdf = plantDepositUseCase.executeForPlant(plantId, date);
        return pdfResponse(pdf, "depositos_planta_" + plantId + "_" + date + ".pdf");
    }

    @Operation(summary = "Reporte consolidado — totales por planta",
               description = "PDF con el total de efectivo, cheques, retenciones, " +
                             "esperado y diferencia por planta, más el gran total.")
    @GetMapping(value = "/consolidated", produces = "application/pdf")
    public ResponseEntity<byte[]> consolidatedReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        byte[] pdf = consolidatedUseCase.execute(date);
        return pdfResponse(pdf, "consolidado_" + date + ".pdf");
    }

    private ResponseEntity<byte[]> pdfResponse(byte[] pdf, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(pdf.length))
                .body(pdf);
    }
}
