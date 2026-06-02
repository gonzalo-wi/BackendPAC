package com.el_jumillano.pac.plants.application;

import com.el_jumillano.pac.plants.domain.PlantCode;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PlantStatsResponse(
        Long plantId,
        String plantName,
        PlantCode plantCode,
        LocalDate date,

        // Conteos por estado
        int totalRoutes,
        int closedRoutes,            // CLOSED → "Cerrado"
        int processedRoutes,         // PROCESSED_* → "Listo" (esperando cierre)
        int awaitingManualItems,     // AWAITING_MANUAL_ITEMS → "Pendiente" (faltan cheques/retenciones)
        int pendingRoutes,           // PENDING + READY_TO_PROCESS + REQUIRES_REVIEW
        int errorRoutes,             // INTEGRATION_ERROR
        double closureRate,          // % de repartos cerrados sobre el total

        // Montos acumulados (de repartos procesados o cerrados)
        BigDecimal totalReceived,
        BigDecimal totalExpected,
        BigDecimal totalDifference,

        // Breakdown de diferencias
        int surplusRoutes,
        int shortageRoutes,
        int noDifferenceRoutes,
        BigDecimal totalSurplusAmount,   // suma de diferencias positivas
        BigDecimal totalShortageAmount   // suma del valor absoluto de diferencias negativas
) {}
