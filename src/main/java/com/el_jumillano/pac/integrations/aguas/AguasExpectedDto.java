package com.el_jumillano.pac.integrations.aguas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO que mapea la respuesta real de Aguas:
 * GET /service1.asmx/reparto_get_valores?idreparto={id}&fecha={dd/MM/yyyy}
 */
@Data
@NoArgsConstructor
public class AguasExpectedDto {

    @JsonProperty("IdReparto")
    private Integer idReparto;

    @JsonProperty("efectivo")
    private BigDecimal efectivo;

    @JsonProperty("Retenciones")
    private BigDecimal retenciones;

    @JsonProperty("Cheques")
    private BigDecimal cheques;

    @JsonProperty("Status")
    private Integer status;
}
