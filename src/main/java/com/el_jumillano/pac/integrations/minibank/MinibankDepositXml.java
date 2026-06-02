package com.el_jumillano.pac.integrations.minibank;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de infraestructura: representa el XML parseado de PIMS (WSDepositsByDayDTO).
 * El dominio nunca ve esta clase.
 */
@Data
@NoArgsConstructor
public class MinibankDepositXml {

    private String depositId;
    private String routeNumber;
    private String date;
    private String time;
    private String amount;
    private String rawXml;
}
