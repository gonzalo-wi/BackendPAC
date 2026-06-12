package com.el_jumillano.pac.reports.infrastructure.pdf;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

@Component
public class PdfMoneyFormatter {

    public String format(BigDecimal amount) {
        if (amount == null) return "$ -";
        return "$ " + buildFormat().format(amount);
    }

    public String formatSigned(BigDecimal amount) {
        if (amount == null) return "$ -";
        if (amount.compareTo(BigDecimal.ZERO) > 0) return "+ $ " + buildFormat().format(amount);
        return "$ " + buildFormat().format(amount);
    }

    private DecimalFormat buildFormat() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        return new DecimalFormat("#,##0.00", symbols);
    }
}
