package com.el_jumillano.pac.reconciliation.application;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ManualValuesRequest(
        @NotNull @PositiveOrZero BigDecimal checksAmount,
        @NotNull @PositiveOrZero BigDecimal withholdingsAmount
) {}
