package com.el_jumillano.pac.reports.infrastructure.pdf;

import com.el_jumillano.pac.differences.domain.DifferenceType;
import com.el_jumillano.pac.reconciliation.domain.ReconciliationStatus;
import com.el_jumillano.pac.reports.domain.PlantReportSection;
import com.el_jumillano.pac.reports.domain.RouteReportLine;
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

class PlantDepositTableBuilder {

    private static final float[] COLUMN_WIDTHS = {7f, 13f, 13f, 13f, 13f, 13f, 14f, 14f};
    private static final String[] HEADERS = {
            "Reparto", "Efectivo Minibank", "Cheques", "Retenciones",
            "Total Recibido", "Total Esperado", "Diferencia", "Estado"
    };

    private final PdfFont regular;
    private final PdfFont bold;
    private final PdfMoneyFormatter formatter;

    PlantDepositTableBuilder(PdfFont regular, PdfFont bold, PdfMoneyFormatter formatter) {
        this.regular   = regular;
        this.bold      = bold;
        this.formatter = formatter;
    }

    Table build(PlantReportSection section) {
        Table table = new Table(UnitValue.createPercentArray(COLUMN_WIDTHS))
                .setWidth(UnitValue.createPercentValue(100));

        addColumnHeaders(table);
        addDataRows(table, section);
        addSubtotalRow(table, section);

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
                            .setPadding(5)
                            .setBorder(Border.NO_BORDER)
                            .add(new Paragraph(label)
                                    .setFont(bold)
                                    .setFontSize(7.5f)
                                    .setFontColor(PdfReportColors.TEXT_WHITE)
                                    .setTextAlignment(TextAlignment.CENTER))
            );
        }
    }

    // -------------------------------------------------------------------------
    // Data rows
    // -------------------------------------------------------------------------

    private void addDataRows(Table table, PlantReportSection section) {
        int index = 0;
        for (RouteReportLine line : section.routes()) {
            Color bg = (index % 2 == 0) ? PdfReportColors.ROW_WHITE : PdfReportColors.ROW_GRAY;
            addDataRow(table, line, bg);
            index++;
        }
    }

    private void addDataRow(Table table, RouteReportLine line, Color bg) {
        table.addCell(centeredCell(String.valueOf(line.routeNumber()), bg, bold));
        table.addCell(moneyCell(formatter.format(line.minibankCash()), bg));
        table.addCell(moneyCell(formatter.format(line.checks()), bg));
        table.addCell(moneyCell(formatter.format(line.retentions()), bg));
        table.addCell(moneyCell(formatter.format(line.totalReceived()), bg));
        table.addCell(moneyCell(formatter.format(line.totalExpected()), bg));
        table.addCell(differenceCell(line.difference(), line.differenceType(), bg, false));
        table.addCell(statusCell(line.status(), bg));
    }

    // -------------------------------------------------------------------------
    // Subtotal row
    // -------------------------------------------------------------------------

    private void addSubtotalRow(Table table, PlantReportSection section) {
        DifferenceType derivedType = deriveDifferenceType(section.subtotalDifference());

        table.addCell(subtotalLabelCell("SUBTOTAL"));
        table.addCell(subtotalMoneyCell(formatter.format(section.subtotalCash())));
        table.addCell(subtotalMoneyCell(formatter.format(section.subtotalChecks())));
        table.addCell(subtotalMoneyCell(formatter.format(section.subtotalRetentions())));
        table.addCell(subtotalMoneyCell(formatter.format(section.subtotalReceived())));
        table.addCell(subtotalMoneyCell(formatter.format(section.subtotalExpected())));
        table.addCell(differenceCell(section.subtotalDifference(), derivedType,
                PdfReportColors.SUBTOTAL_BG, true));
        table.addCell(subtotalEmptyCell());
    }

    // -------------------------------------------------------------------------
    // Cell factories
    // -------------------------------------------------------------------------

    private Cell centeredCell(String text, Color bg, PdfFont font) {
        return baseCell(bg)
                .add(new Paragraph(text)
                        .setFont(font)
                        .setFontSize(8)
                        .setFontColor(PdfReportColors.TEXT_DARK)
                        .setTextAlignment(TextAlignment.CENTER));
    }

    private Cell moneyCell(String text, Color bg) {
        return baseCell(bg)
                .add(new Paragraph(text)
                        .setFont(regular)
                        .setFontSize(8)
                        .setFontColor(PdfReportColors.TEXT_DARK)
                        .setTextAlignment(TextAlignment.RIGHT));
    }

    private Cell differenceCell(BigDecimal amount, DifferenceType type, Color bg, boolean isBold) {
        Color textColor = resolveDifferenceColor(type);
        PdfFont font    = isBold ? bold : regular;
        return baseCell(bg)
                .add(new Paragraph(formatter.formatSigned(amount))
                        .setFont(font)
                        .setFontSize(8)
                        .setFontColor(textColor)
                        .setTextAlignment(TextAlignment.RIGHT));
    }

    private Cell statusCell(ReconciliationStatus status, Color bg) {
        return baseCell(bg)
                .add(new Paragraph(translateStatus(status))
                        .setFont(regular)
                        .setFontSize(7)
                        .setFontColor(PdfReportColors.TEXT_DARK)
                        .setTextAlignment(TextAlignment.CENTER));
    }

    private Cell subtotalLabelCell(String label) {
        return subtotalBase()
                .add(new Paragraph(label)
                        .setFont(bold)
                        .setFontSize(8)
                        .setFontColor(PdfReportColors.NAVY)
                        .setTextAlignment(TextAlignment.CENTER));
    }

    private Cell subtotalMoneyCell(String text) {
        return subtotalBase()
                .add(new Paragraph(text)
                        .setFont(bold)
                        .setFontSize(8)
                        .setFontColor(PdfReportColors.NAVY)
                        .setTextAlignment(TextAlignment.RIGHT));
    }

    private Cell subtotalEmptyCell() {
        return subtotalBase();
    }

    // -------------------------------------------------------------------------
    // Base cell styles
    // -------------------------------------------------------------------------

    private Cell baseCell(Color bg) {
        return new Cell()
                .setBackgroundColor(bg)
                .setPadding(4)
                .setBorderLeft(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(PdfReportColors.BORDER, 0.5f));
    }

    private Cell subtotalBase() {
        return new Cell()
                .setBackgroundColor(PdfReportColors.SUBTOTAL_BG)
                .setPadding(5)
                .setBorder(Border.NO_BORDER)
                .setBorderTop(new SolidBorder(PdfReportColors.BLUE, 1f));
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

    private String translateStatus(ReconciliationStatus status) {
        if (status == null) return "-";
        return switch (status) {
            case PENDING                       -> "Pendiente";
            case READY_TO_PROCESS              -> "Listo p/ procesar";
            case AWAITING_MANUAL_ITEMS         -> "Esp. items manuales";
            case PROCESSED_WITH_SURPLUS        -> "Sobrante";
            case PROCESSED_WITH_SHORTAGE       -> "Faltante";
            case PROCESSED_WITHOUT_DIFFERENCE  -> "Sin diferencia";
            case REQUIRES_REVIEW               -> "Revisión";
            case INTEGRATION_ERROR             -> "Error integración";
            case QUEUED_FOR_CLOSE              -> "En cola cierre";
            case CLOSED                        -> "Cerrado";
        };
    }
}
