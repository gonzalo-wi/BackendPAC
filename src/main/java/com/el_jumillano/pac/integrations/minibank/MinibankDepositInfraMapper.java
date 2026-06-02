package com.el_jumillano.pac.integrations.minibank;

import com.el_jumillano.pac.deposits.domain.Deposit;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Convierte MinibankDepositXml (DTO de infraestructura) a Deposit (dominio).
 */
@Component
public class MinibankDepositInfraMapper {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public Deposit toDomain(MinibankDepositXml xml, Long resolvedPlantId, Long resolvedCashierId) {
        return Deposit.builder()
                .externalDepositId(xml.getDepositId())
                .routeNumber(Integer.parseInt(xml.getRouteNumber()))
                .plantId(resolvedPlantId)
                .cashierId(resolvedCashierId)
                .depositDate(LocalDate.parse(xml.getDate(), DATE_FMT))
                .depositTime(xml.getTime() != null ? LocalTime.parse(xml.getTime(), TIME_FMT) : null)
                .amount(new BigDecimal(xml.getAmount()))
                .rawPayload(xml.getRawXml())
                .build();
    }
}
