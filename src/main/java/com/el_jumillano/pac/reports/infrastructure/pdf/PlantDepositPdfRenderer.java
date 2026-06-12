package com.el_jumillano.pac.reports.infrastructure.pdf;

import com.el_jumillano.pac.reports.domain.PlantReportSection;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PlantDepositPdfRenderer {

    private static final String REPORT_TITLE    = "Reporte de Depósitos";
    private static final String REPORT_SUBTITLE = "Detalle por planta y reparto — ordenado por número de reparto ascendente";
    private static final String FOOTER_LABEL    = "PAC · Reporte de Depósitos por Planta";

    private final PdfMoneyFormatter moneyFormatter;

    public byte[] render(List<PlantReportSection> sections, LocalDate date) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfFont regular = PdfReportFonts.regular();
        PdfFont bold    = PdfReportFonts.bold();

        PdfWriter   writer  = createWriter(out);
        PdfDocument pdfDoc  = new PdfDocument(writer);

        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE,
                new PdfPageEventHandler(regular, FOOTER_LABEL));

        Document document = new Document(pdfDoc, PageSize.A4.rotate());
        document.setMargins(36, 36, 50, 36);

        PdfReportHeader.render(document, REPORT_TITLE, REPORT_SUBTITLE, date, regular, bold);

        PlantDepositTableBuilder tableBuilder =
                new PlantDepositTableBuilder(regular, bold, moneyFormatter);

        for (PlantReportSection section : sections) {
            renderSection(document, section, tableBuilder, regular, bold);
        }

        document.close();
        return out.toByteArray();
    }

    private void renderSection(Document document, PlantReportSection section,
                               PlantDepositTableBuilder tableBuilder,
                               PdfFont regular, PdfFont bold) {
        document.add(plantBanner(section.plantName(), bold));

        if (section.routes().isEmpty()) {
            document.add(emptyNotice(regular));
        } else {
            document.add(tableBuilder.build(section));
        }

        document.add(sectionSpacer());
    }

    private Paragraph plantBanner(String plantName, PdfFont bold) {
        return new Paragraph("   " + plantName.toUpperCase())
                .setFont(bold)
                .setFontSize(11)
                .setFontColor(PdfReportColors.TEXT_WHITE)
                .setBackgroundColor(PdfReportColors.PLANT_BANNER)
                .setPaddingTop(7)
                .setPaddingBottom(7)
                .setMarginTop(14)
                .setMarginBottom(0);
    }

    private Paragraph emptyNotice(PdfFont regular) {
        return new Paragraph(
                "No hay conciliaciones registradas para esta planta en la fecha seleccionada.")
                .setFont(regular)
                .setFontSize(8)
                .setFontColor(PdfReportColors.TEXT_MUTED)
                .setMarginTop(5)
                .setMarginBottom(5);
    }

    private Table sectionSpacer() {
        return new Table(1)
                .setWidth(UnitValue.createPercentValue(100))
                .setHeight(12)
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
    }

    private PdfWriter createWriter(ByteArrayOutputStream out) {
        return new PdfWriter(out);
    }
}
