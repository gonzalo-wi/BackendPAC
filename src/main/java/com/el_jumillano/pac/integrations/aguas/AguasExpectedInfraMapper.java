package com.el_jumillano.pac.integrations.aguas;

import com.el_jumillano.pac.expected.domain.ExpectedAmount;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class AguasExpectedInfraMapper {

    /**
     * Convierte el DTO real de Aguas a dominio.
     * expectedTotal = efectivo + cheques + retenciones.
     * La fecha no viene en el DTO, se pasa como parámetro.
     */
    public ExpectedAmount toDomain(AguasExpectedDto dto, LocalDate date, Long plantId, int version) {
        BigDecimal efectivo     = nullSafe(dto.getEfectivo());
        BigDecimal cheques      = nullSafe(dto.getCheques());
        BigDecimal retenciones  = nullSafe(dto.getRetenciones());
        BigDecimal total        = efectivo.add(cheques).add(retenciones);

        return ExpectedAmount.builder()
                .routeNumber(dto.getIdReparto())
                .plantId(plantId)
                .date(date)
                .expectedCash(efectivo)
                .expectedChecks(cheques)
                .expectedWithholdings(retenciones)
                .expectedTotal(total)
                .version(version)
                .current(true)
                .fetchedAt(LocalDateTime.now())
                .build();
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
