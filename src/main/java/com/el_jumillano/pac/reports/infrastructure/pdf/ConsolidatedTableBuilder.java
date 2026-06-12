package com.el_jumillano.pac.reports.infrastructure.pdf;

import com.el_jumillano.pac.differences.domain.DifferenceType;
import com.el_jumillano.pac.reports.domain.ConsolidatedReportData;
import com.el_jumillano.pac.reports.domain.PlantReportSection;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.math.BigDecimal;
import java.util.List;

class ConsolidatedTableBuilder {

    private static final float[] COLUMN_WIDTHS = {18f, 14f, 13f, 13f, 14f, 14f, 14f};
    private static final String[] HEADERS = {
            "Planta", "Efectivo Minibank", "Cheques", "Retenciones",
            "Total Recibido", "Total Esperado", "Diferencia"
    };

    private final PdfFont regular;
    private final PdfFont bold;
    private final PdfMoneyFormatter formatter;

    ConsolidatedTableBuilder(PdfFont regular, PdfFont bold, PdfMoneyFormatter formatter) {
        this.regular   = regular;
        this.bold      = bold;
        this.formatter = formatter;
    }

    Table build(ConsolidatedReportData data) {
        Table table = new Table(UnitValue.createPercentArray(COLUMN_WIDTHS))
                .setWidth(UnitValue.createPercentValue(100));

        addColumnHeaders(table);
        addPlantRows(table, data.sections());
        addGrandTotalRow(table, data);

        return table;
    }

    // -------------------------------------------------------------------------
    // Header row
    // -------------------------------------------------------------------------

    private void addColumnHeaders(Table table) {
        for (String label : HEADERS) {
            table.addHeaderCell(
                    new Cell()
                            .setBackgroundColor(PdfReportColors.NAVY)
                            .setPaddingTop(6)
                            .setPaddingBottom(6)
                            .setPaddingLeft(6)
                            .setPaddingRight(6)
                            .setBorder(Border.NO_BORDER)
                            .add(new Paragraph(label)
                                    .setFont(bold)
                                    .setFontSize(8.5f)
                                    .setFontColor(PdfReportColors.TEXT_WHITE)
                                    .setTextAlignment(TextAlignment.CENTER))
            );
        }
    }

    // -------------------------------------------------------------------------
    // Plant rows
    // -------------------------------------------------------------------------

    private void addPlantRows(Table table, List<PlantReportSection> sections) {
        int index = 0;
        for (PlantReportSection section : sections) {
            Color bg = (index % 2 == 0) ? PdfReportColors.ROW_WHITE : PdfReportColors.ROW_GRAY;
            addPlantRow(table, section, bg);
            index++;
        }
    }

    private void addPlantRow(Table table, PlantReportSection section, Color bg) {
        DifferenceType type = deriveDifferenceType(section.subtotalDifference());

        table.addCell(textCell(section.plantName(), bg, TextAlignment.LEFT, bold));
        table.addCell(moneyCell(formatter.format(section.subtotalCash()), bg));
        table.addCell(moneyCell(formatter.format(section.subtotalChecks()), bg));
        table.addCell(moneyCell(formatter.format(section.subtotalRetentions()), bg));
        table.addCell(moneyCell(formatter.format(section.subtotalReceived()), bg));
        table.addCell(moneyCell(formatter.format(section.subtotalExpected()), bg));
        table.addCell(differenceCell(section.subtotalDifference(), type, bg, false));
    }

    // -------------------------------------------------------------------------
    // Grand total row
    // -------------------------------------------------------------------------

    private void addGrandTotalRow(Table table, ConsolidatedReportData data) {
        DifferenceType type = deriveDifferenceType(data.grandTotalDifference());

        table.addCell(grandTotalLabelCell("TOTAL GENERAL"));
        table.addCell(grandTotalMoneyCell(formatter.format(data.grandTotalCash())));
        table.addCell(grandTotalMoneyCell(formatter.format(data.grandTotalChecks())));
        table.addCell(grandTotalMoneyCell(formatter.format(data.grandTotalRetentions())));
        table.addCell(grandTotalMoneyCell(formatter.format(data.grandTotalReceived())));
        table.addCell(grandTotalMoneyCell(formatter.format(data.grandTotalExpected())));
        table.addCell(grandTotalDifferenceCell(data.grandTotalDifference(), type));
    }

    // -------------------------------------------------------------------------
    // Cell factories — data
    // -------------------------------------------------------------------------

    private Cell textCell(String text, Color bg, TextAlignment align, PdfFont font) {
        return baseCell(bg)
                .add(new Paragraph(text)
                        .setFont(font)
                        .setFontSize(9)
                        .setFontColor(PdfReportColors.TEXT_DARK)
                        .setTextAlignment(align));
    }

    private Cell moneyCell(String text, Color bg) {
        return baseCell(bg)
                .add(new Paragraph(text)
                        .setFont(regular)
                        .setFontSize(9)
                        .setFontColor(PdfReportColors.TEXT_DARK)
                        .setTextAlignment(TextAlignment.RIGHT));
    }

    private Cell differenceCell(BigDecimal amount, DifferenceType type, Color bg, boolean isBold) {
        Color textColor = resolveDifferenceColor(type);
        PdfFont font    = isBold ? bold : regular;
        return baseCell(bg)
                .add(new Paragraph(formatter.formatSigned(amount))
                        .setFont(font)
                        .setFontSize(9)
                        .setFontColor(textColor)
                        .setTextAlignment(TextAlignment.RIGHT));
    }

    // -------------------------------------------------------------------------
    // Cell factories — grand total
    // -------------------------------------------------------------------------

    private Cell grandTotalLabelCell(String label) {
        return grandTotalBase()
                .add(new Paragraph(label)
                        .setFont(bold)
                        .setFontSize(10)
                        .setFontColor(PdfReportColors.TEXT_WHITE)
                        .setTextAlignment(TextAlignment.LEFT));
    }

    private Cell grandTotalMoneyCell(String text) {
        return grandTotalBase()
                .add(new Paragraph(text)
                        .setFont(bold)
                        .setFontSize(10)
                        .setFontColor(PdfReportColors.TEXT_WHITE)
                        .setTextAlignment(TextAlignment.RIGHT));
    }

    private Cell grandTotalDifferenceCell(BigDecimal amount, DifferenceType type) {
        Color textColor = resolveDifferenceColor(type);
        return grandTotalBase()
                .add(new Paragraph(formatter.formatSigned(amount))
                        .setFont(bold)
                        .setFontSize(11)
                        .setFontColor(textColor)
                        .setTextAlignment(TextAlignment.RIGHT));
    }

    // -------------------------------------------------------------------------
    // Base cell styles
    // -------------------------------------------------------------------------

    private Cell baseCell(Color bg) {
        return new Cell()
                .setBackgroundColor(bg)
                .setPaddingTop(5)
                .setPaddingBottom(5)
                .setPaddingLeft(6)
                .setPaddingRight(6)
                .setBorderLeft(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(PdfReportColors.BORDER, 0.5f));
    }

    private Cell grandTotalBase() {
        return new Cell()
                .setBackgroundColor(PdfReportColors.GRAND_TOTAL_BG)
                .setPaddingTop(7)
                .setPaddingBottom(7)
                .setPaddingLeft(6)
                .setPaddingRight(6)
                .setBorder(Border.NO_BORDER)
                .setBorderTop(new SolidBorder(PdfReportColors.BLUE, 2f));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Color resolveDifferenceColor(DifferenceType type) {
        if (type == null) return PdfReportColors.NEUTRAL;
        return switch (type) {
            case SURPLUS_NORMAL           -> PdfReportColors.SURPLUS;
            case SURPLUS_HIGH             -> PdfReportColors.SURPLUS_HIGH;
            case SHORTAGE, SHORTAGE_CRITICAL -> PdfReportColors.SHORTAGE;
            case NONE                     -> PdfReportColors.NEUTRAL;
        };
    }

    private DifferenceType deriveDifferenceType(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) return DifferenceType.NONE;
        return amount.compareTo(BigDecimal.ZERO) > 0
                ? DifferenceType.SURPLUS_NORMAL
                : DifferenceType.SHORTAGE;
    }
}
