package com.el_jumillano.pac.reports.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ConsolidatedReportData(
        LocalDate date,
        List<PlantReportSection> sections,
        BigDecimal grandTotalCash,
        BigDecimal grandTotalChecks,
        BigDecimal grandTotalRetentions,
        BigDecimal grandTotalReceived,
        BigDecimal grandTotalExpected,
        BigDecimal grandTotalDifference
) {}
