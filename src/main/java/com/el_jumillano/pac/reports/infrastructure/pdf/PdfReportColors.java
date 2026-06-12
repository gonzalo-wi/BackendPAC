package com.el_jumillano.pac.reports.infrastructure.pdf;

import com.itextpdf.kernel.colors.DeviceRgb;

public final class PdfReportColors {

    // Paleta principal
    public static final DeviceRgb NAVY        = new DeviceRgb(0x1B, 0x2A, 0x4A);
    public static final DeviceRgb BLUE        = new DeviceRgb(0x2E, 0x86, 0xC1);
    public static final DeviceRgb BLUE_LIGHT  = new DeviceRgb(0xD6, 0xEA, 0xF8);

    // Fondos de fila
    public static final DeviceRgb ROW_WHITE   = new DeviceRgb(0xFF, 0xFF, 0xFF);
    public static final DeviceRgb ROW_GRAY    = new DeviceRgb(0xF4, 0xF6, 0xF7);

    // Filas especiales
    public static final DeviceRgb SUBTOTAL_BG     = new DeviceRgb(0xD6, 0xEA, 0xF8);
    public static final DeviceRgb GRAND_TOTAL_BG  = new DeviceRgb(0x1B, 0x2A, 0x4A);
    public static final DeviceRgb PLANT_BANNER     = new DeviceRgb(0x21, 0x61, 0x8A);

    // Semáforo de diferencias
    public static final DeviceRgb SURPLUS        = new DeviceRgb(0x1E, 0x8B, 0x4C);
    public static final DeviceRgb SURPLUS_HIGH   = new DeviceRgb(0xCA, 0x6F, 0x1E);
    public static final DeviceRgb SHORTAGE       = new DeviceRgb(0xCB, 0x43, 0x35);
    public static final DeviceRgb NEUTRAL        = new DeviceRgb(0x7F, 0x8C, 0x8D);

    // Texto
    public static final DeviceRgb TEXT_DARK  = new DeviceRgb(0x17, 0x20, 0x2A);
    public static final DeviceRgb TEXT_WHITE = new DeviceRgb(0xFF, 0xFF, 0xFF);
    public static final DeviceRgb TEXT_MUTED = new DeviceRgb(0x7F, 0x8C, 0x8D);

    // Borde
    public static final DeviceRgb BORDER = new DeviceRgb(0xD5, 0xD8, 0xDC);

    private PdfReportColors() {}
}
