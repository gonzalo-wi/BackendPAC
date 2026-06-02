package com.el_jumillano.pac.deposits.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateWithholdingRequest(
        @NotBlank String concepto,
        String withholdingNumber,
        Long accountNumber,
        String paymentDate,
        String type,
        @NotNull @Positive BigDecimal amount
) {}
