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
public class ManualValues {

    private Long id;
    private Integer routeNumber;
    private Long plantId;
    private LocalDate date;
    private BigDecimal checksAmount;
    private BigDecimal withholdingsAmount;
    private String enteredBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
