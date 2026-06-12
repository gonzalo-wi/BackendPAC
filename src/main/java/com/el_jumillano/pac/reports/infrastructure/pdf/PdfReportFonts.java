package com.el_jumillano.pac.reports.infrastructure.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;

import java.io.IOException;

public final class PdfReportFonts {

    private PdfReportFonts() {}

    public static PdfFont regular() {
        return create(StandardFonts.HELVETICA);
    }

    public static PdfFont bold() {
        return create(StandardFonts.HELVETICA_BOLD);
    }

    public static PdfFont italic() {
        return create(StandardFonts.HELVETICA_OBLIQUE);
    }

    private static PdfFont create(String name) {
        try {
            return PdfFontFactory.createFont(name);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar la fuente: " + name, e);
        }
    }
}
