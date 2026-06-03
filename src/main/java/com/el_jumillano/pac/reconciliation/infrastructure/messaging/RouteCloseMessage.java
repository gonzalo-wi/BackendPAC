package com.el_jumillano.pac.reconciliation.infrastructure.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteCloseMessage {

    private Long reconciliationId;
    private Integer routeNumber;
    private Long plantId;
    private LocalDate date;
    private BigDecimal expectedAmount;
    private String userId;
}
