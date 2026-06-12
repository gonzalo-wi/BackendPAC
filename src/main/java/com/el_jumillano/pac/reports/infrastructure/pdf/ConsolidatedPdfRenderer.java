package com.el_jumillano.pac.reports.infrastructure.pdf;

import com.el_jumillano.pac.reports.domain.ConsolidatedReportData;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
@RequiredArgsConstructor
public class ConsolidatedPdfRenderer {

    private static final String REPORT_TITLE    = "Reporte Consolidado";
    private static final String REPORT_SUBTITLE = "Totales por planta — todas las plantas";
    private static final String FOOTER_LABEL    = "PAC · Reporte Consolidado por Planta";

    private final PdfMoneyFormatter moneyFormatter;

    public byte[] render(ConsolidatedReportData data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfFont regular = PdfReportFonts.regular();
        PdfFont bold    = PdfReportFonts.bold();

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(out));
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE,
                new PdfPageEventHandler(regular, FOOTER_LABEL));

        Document document = new Document(pdfDoc, PageSize.A4.rotate());
        document.setMargins(36, 36, 50, 36);

        PdfReportHeader.render(document, REPORT_TITLE, REPORT_SUBTITLE, data.date(), regular, bold);

        ConsolidatedTableBuilder tableBuilder =
                new ConsolidatedTableBuilder(regular, bold, moneyFormatter);

        document.add(tableBuilder.build(data));
        document.close();

        return out.toByteArray();
    }
}
