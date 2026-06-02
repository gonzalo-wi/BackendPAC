package com.el_jumillano.pac.deposits.application;

import java.math.BigDecimal;

public record WithholdingResponse(
        Long id,
        Long depositId,
        String concepto,
        String withholdingNumber,
        Long accountNumber,
        String paymentDate,
        String type,
        BigDecimal amount
) {}
