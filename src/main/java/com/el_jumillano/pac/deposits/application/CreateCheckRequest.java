package com.el_jumillano.pac.deposits.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateCheckRequest(
        @NotBlank String concepto,
        String bank,
        String branch,
        String locality,
        String checkNumber,
        Long accountNumber,
        Integer accountCode,
        String holder,
        String paymentDate,
        @NotNull @Positive BigDecimal amount
) {}
