package com.el_jumillano.pac.differences.domain;

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
public class DifferenceRecord {

    private Long id;
    private Long reconciliationId;
    private Integer routeNumber;
    private Long plantId;
    private LocalDate date;
    private String employeeId;
    private BigDecimal amount;
    private DifferenceType type;
    private String externalReference;
    private boolean notified;
    private LocalDateTime createdAt;
}
