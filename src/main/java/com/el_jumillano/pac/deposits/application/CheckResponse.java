package com.el_jumillano.pac.deposits.application;

import java.math.BigDecimal;

public record CheckResponse(
        Long id,
        Long depositId,
        String concepto,
        String bank,
        String branch,
        String locality,
        String checkNumber,
        Long accountNumber,
        Integer accountCode,
        String holder,
        String paymentDate,
        BigDecimal amount
) {}
