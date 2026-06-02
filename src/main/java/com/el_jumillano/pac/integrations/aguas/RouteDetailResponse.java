package com.el_jumillano.pac.integrations.aguas;

import java.math.BigDecimal;
import java.util.List;

public record RouteDetailResponse(
        Integer routeNumber,
        String date,
        BigDecimal total,
        List<ClientPaymentResponse> payments
) {
    public record ClientPaymentResponse(
            String nrocta,
            List<PaymentItemResponse> items
    ) {}

    public record PaymentItemResponse(
            BigDecimal amount,
            String paymentType  // "Efectivo" | "Cheque" | "Retencion" | etc.
    ) {}
}
