package com.el_jumillano.pac.integrations.aguas;

import com.el_jumillano.pac.expected.domain.ExpectedAmount;
import com.el_jumillano.pac.reconciliation.domain.CloseRouteResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface AguasClient {

    /** Obtiene el esperado de un reparto específico en una fecha. */
    ExpectedAmount getExpectedByRoute(LocalDate date, Integer routeNumber);

    /** Obtiene todos los repartos que ya tienen valor en Aguas para una fecha (idreparto=0). */
    List<ExpectedAmount> getAllExpectedByDate(LocalDate date);

    CloseRouteResult closeRouteWithExpectedAmount(LocalDate date, Integer routeNumber, BigDecimal expectedAmount);
}
