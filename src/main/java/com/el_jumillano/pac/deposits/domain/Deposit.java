package com.el_jumillano.pac.deposits.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Deposit {

    private Long id;
    private String externalDepositId;
    private Integer routeNumber;
    private Long plantId;
    private Long cashierId;
    private LocalDate depositDate;
    private LocalTime depositTime;
    private BigDecimal amount;
    private String rawPayload;
    private LocalDateTime createdAt;
}
