package com.el_jumillano.pac.expected.domain;

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
public class ExpectedAmount {

    private Long id;
    private Integer routeNumber;
    private Long plantId;
    private LocalDate date;
    private BigDecimal expectedCash;
    private BigDecimal expectedChecks;
    private BigDecimal expectedWithholdings;
    private BigDecimal expectedTotal;
    private Integer version;
    private boolean current;
    private LocalDateTime fetchedAt;
}
