package com.el_jumillano.pac.reconciliation.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reconciliation {

    private Long id;
    private Integer routeNumber;
    private Long plantId;
    private LocalDate date;
    private ReconciliationStatus status;

    // Valores recibidos realmente
    private BigDecimal minibankCashTotal;
    private BigDecimal manualChecksTotal;
    private BigDecimal manualWithholdingsTotal;
    private BigDecimal totalReceived;

    // Esperado de Aguas (snapshot al momento del procesamiento)
    private BigDecimal aguasExpectedCash;
    private BigDecimal aguasExpectedChecks;
    private BigDecimal aguasExpectedWithholdings;
    private BigDecimal aguasExpectedTotal;

    // Diferencia
    private BigDecimal differenceAmount;
    private com.el_jumillano.pac.differences.domain.DifferenceType differenceType;

    // Procesamiento
    private String processedBy;
    private LocalDateTime processedAt;
    private LocalDateTime closedAt;

    // Lo que PAC envió a Aguas (siempre igual a aguasExpectedTotal)
    private BigDecimal aguasSentAmount;
    private String aguasResponse;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
