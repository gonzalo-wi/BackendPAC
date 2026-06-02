package com.el_jumillano.pac.reconciliation.application;

import com.el_jumillano.pac.differences.domain.DifferenceType;
import com.el_jumillano.pac.reconciliation.domain.ReconciliationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReconciliationResponse(
        Long id,
        Integer routeNumber,
        Long plantId,
        LocalDate date,
        ReconciliationStatus status,

        // Lo recibido
        BigDecimal minibankCashTotal,
        BigDecimal manualChecksTotal,
        BigDecimal manualWithholdingsTotal,
        BigDecimal totalReceived,

        // Lo esperado por Aguas (desglose completo)
        BigDecimal aguasExpectedCash,
        BigDecimal aguasExpectedChecks,
        BigDecimal aguasExpectedWithholdings,
        BigDecimal aguasExpectedTotal,

        // Diferencia
        BigDecimal differenceAmount,
        DifferenceType differenceType,

        // Procesamiento
        String processedBy,
        LocalDateTime processedAt,
        LocalDateTime closedAt
) {}
