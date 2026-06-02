package com.el_jumillano.pac.deposits.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositAdjustment {

    private Long id;
    private Long originalDepositId;
    private Integer fromRouteNumber;
    private Integer toRouteNumber;
    private BigDecimal amount;
    private String reason;
    private String createdBy;
    private LocalDateTime createdAt;
}
