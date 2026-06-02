package com.el_jumillano.pac.integrations.aguas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class AguasPaymentItemDto {

    @JsonProperty("monto")
    private BigDecimal monto;

    @JsonProperty("tipoValor")
    private String tipoValor;
}
