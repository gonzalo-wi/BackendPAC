package com.el_jumillano.pac.reports.infrastructure.pdf;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PdfPageEventHandler implements IEventHandler {

    private static final float MARGIN        = 28f;
    private static final float FOOTER_Y      = 16f;
    private static final float LINE_Y        = 30f;
    private static final float FONT_SIZE     = 7f;
    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PdfFont font;
    private final String reportTitle;
    private final String generatedAt;

    public PdfPageEventHandler(PdfFont font, String reportTitle) {
        this.font        = font;
        this.reportTitle = reportTitle;
        this.generatedAt = "Generado: " + LocalDateTime.now().format(TS_FORMAT);
    }

    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfPage     page    = docEvent.getPage();
        PdfDocument pdfDoc  = docEvent.getDocument();
        Rectangle   size    = page.getPageSize();

        PdfCanvas pdfCanvas = new PdfCanvas(
                page.newContentStreamAfter(), page.getResources(), pdfDoc);

        drawSeparatorLine(pdfCanvas, size);

        try (Canvas canvas = new Canvas(pdfCanvas, size)) {
            canvas.showTextAligned(
                    styledParagraph(reportTitle),
                    MARGIN, FOOTER_Y, TextAlignment.LEFT);

            canvas.showTextAligned(
                    styledParagraph("Página " + pdfDoc.getPageNumber(page)),
                    size.getWidth() / 2, FOOTER_Y, TextAlignment.CENTER);

            canvas.showTextAligned(
                    styledParagraph(generatedAt),
                    size.getWidth() - MARGIN, FOOTER_Y, TextAlignment.RIGHT);
        }

        pdfCanvas.release();
    }

    private void drawSeparatorLine(PdfCanvas canvas, Rectangle size) {
        canvas.saveState()
                .setStrokeColor(PdfReportColors.BORDER)
                .setLineWidth(0.5f)
                .moveTo(MARGIN, LINE_Y)
                .lineTo(size.getWidth() - MARGIN, LINE_Y)
                .stroke()
                .restoreState();
    }

    private Paragraph styledParagraph(String text) {
        return new Paragraph(text)
                .setFont(font)
                .setFontSize(FONT_SIZE)
                .setFontColor(PdfReportColors.TEXT_MUTED);
    }
}
