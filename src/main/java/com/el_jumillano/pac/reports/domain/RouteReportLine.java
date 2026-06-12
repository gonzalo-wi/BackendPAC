package com.el_jumillano.pac.reports.domain;

import com.el_jumillano.pac.differences.domain.DifferenceType;
import com.el_jumillano.pac.reconciliation.domain.ReconciliationStatus;

import java.math.BigDecimal;

public record RouteReportLine(
        Integer routeNumber,
        BigDecimal minibankCash,
        BigDecimal checks,
        BigDecimal retentions,
        BigDecimal totalReceived,
        BigDecimal totalExpected,
        BigDecimal difference,
        DifferenceType differenceType,
        ReconciliationStatus status
) {}
