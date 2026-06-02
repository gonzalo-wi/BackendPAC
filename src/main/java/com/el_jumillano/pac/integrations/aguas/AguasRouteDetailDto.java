package com.el_jumillano.pac.integrations.aguas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class AguasRouteDetailDto {

    @JsonProperty("total")
    private BigDecimal total;

    @JsonProperty("resultado")
    private List<AguasClientPaymentDto> resultado;

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("reparto")
    private Integer reparto;
}
