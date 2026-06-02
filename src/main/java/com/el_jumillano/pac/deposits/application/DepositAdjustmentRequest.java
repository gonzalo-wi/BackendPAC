package com.el_jumillano.pac.deposits.application;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DepositAdjustmentRequest(
        @NotNull Integer toRouteNumber,
        @NotNull @Positive BigDecimal amount,
        @NotNull String reason
) {}
