package com.el_jumillano.pac.reports.infrastructure.pdf;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public final class PdfReportHeader {

    private static final DateTimeFormatter DATE_FMT  = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String            LOGO_PATH = "images/blanco.png";
    private static final float             LOGO_MAX_WIDTH  = 90f;
    private static final float             LOGO_MAX_HEIGHT = 50f;
    private static final float[] COL_WIDTHS = {16f, 54f, 30f};

    private PdfReportHeader() {}

    public static void render(Document document, String title, String subtitle,
                              LocalDate date, PdfFont regular, PdfFont bold) {

        Table header = new Table(UnitValue.createPercentArray(COL_WIDTHS))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(14);

        header.addCell(buildLogoCell());
        header.addCell(buildTitleCell(title, subtitle, regular, bold));
        header.addCell(buildDateCell(date, regular, bold));

        document.add(header);
        document.add(buildAccentLine());
    }

    // -------------------------------------------------------------------------
    // Logo cell
    // -------------------------------------------------------------------------

    private static Cell buildLogoCell() {
        Cell cell = new Cell()
                .setBackgroundColor(PdfReportColors.NAVY)
                .setBorder(Border.NO_BORDER)
                .setPaddingLeft(16)
                .setPaddingRight(8)
                .setPaddingTop(14)
                .setPaddingBottom(14)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setHorizontalAlignment(HorizontalAlignment.LEFT);

        Image logo = loadLogo();
        if (logo != null) {
            cell.add(logo);
        }

        return cell;
    }

    private static Image loadLogo() {
        try {
            URL resource = PdfReportHeader.class.getClassLoader().getResource(LOGO_PATH);
            if (resource == null) {
                log.warn("Logo no encontrado en classpath: {}", LOGO_PATH);
                return null;
            }
            ImageData imageData = ImageDataFactory.create(resource);
            Image image = new Image(imageData);
            image.scaleToFit(LOGO_MAX_WIDTH, LOGO_MAX_HEIGHT);
            return image;
        } catch (Exception e) {
            log.warn("No se pudo cargar el logo '{}': {}", LOGO_PATH, e.getMessage());
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Title cell
    // -------------------------------------------------------------------------

    private static Cell buildTitleCell(String title, String subtitle,
                                       PdfFont regular, PdfFont bold) {
        Cell cell = new Cell()
                .setBackgroundColor(PdfReportColors.NAVY)
                .setBorder(Border.NO_BORDER)
                .setPaddingLeft(8)
                .setPaddingTop(14)
                .setPaddingBottom(14)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        cell.add(new Paragraph("PAC · Sistema de Conciliación")
                .setFont(regular)
                .setFontSize(7.5f)
                .setFontColor(PdfReportColors.BLUE)
                .setMarginBottom(3));

        cell.add(new Paragraph(title)
                .setFont(bold)
                .setFontSize(18)
                .setFontColor(PdfReportColors.TEXT_WHITE)
                .setMarginBottom(3));

        cell.add(new Paragraph(subtitle)
                .setFont(regular)
                .setFontSize(8.5f)
                .setFontColor(PdfReportColors.TEXT_MUTED));

        return cell;
    }

    // -------------------------------------------------------------------------
    // Date cell
    // -------------------------------------------------------------------------

    private static Cell buildDateCell(LocalDate date, PdfFont regular, PdfFont bold) {
        Cell cell = new Cell()
                .setBackgroundColor(PdfReportColors.NAVY)
                .setBorder(Border.NO_BORDER)
                .setPaddingRight(16)
                .setPaddingTop(14)
                .setPaddingBottom(14)
                .setTextAlignment(TextAlignment.RIGHT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        cell.add(new Paragraph("Fecha del reporte")
                .setFont(regular)
                .setFontSize(7.5f)
                .setFontColor(PdfReportColors.TEXT_MUTED)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(3));

        cell.add(new Paragraph(date.format(DATE_FMT))
                .setFont(bold)
                .setFontSize(18)
                .setFontColor(PdfReportColors.TEXT_WHITE)
                .setTextAlignment(TextAlignment.RIGHT));

        return cell;
    }

    // -------------------------------------------------------------------------
    // Accent line
    // -------------------------------------------------------------------------

    private static Table buildAccentLine() {
        return new Table(1)
                .setWidth(UnitValue.createPercentValue(100))
                .setHeight(3)
                .setBackgroundColor(PdfReportColors.BLUE)
                .setMarginBottom(14);
    }
}
