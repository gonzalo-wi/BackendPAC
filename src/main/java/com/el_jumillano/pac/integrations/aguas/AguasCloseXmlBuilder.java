package com.el_jumillano.pac.integrations.aguas;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Construye el XML de cierre que PAC envía a Aguas.
 * Siempre usa el expectedTotal de Aguas, nunca el totalReceived de PAC.
 */
@Component
public class AguasCloseXmlBuilder {

    public String build(LocalDate date, Integer routeNumber, BigDecimal expectedAmount) {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <closeRoute>
                    <date>%s</date>
                    <routeNumber>%d</routeNumber>
                    <amount>%s</amount>
                </closeRoute>
                """.formatted(date, routeNumber, expectedAmount.toPlainString());
    }
}
