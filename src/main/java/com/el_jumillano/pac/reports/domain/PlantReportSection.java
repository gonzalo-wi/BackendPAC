package com.el_jumillano.pac.reports.domain;

import java.math.BigDecimal;
import java.util.List;

public record PlantReportSection(
        Long plantId,
        String plantName,
        List<RouteReportLine> routes,
        BigDecimal subtotalCash,
        BigDecimal subtotalChecks,
        BigDecimal subtotalRetentions,
        BigDecimal subtotalReceived,
        BigDecimal subtotalExpected,
        BigDecimal subtotalDifference
) {}
