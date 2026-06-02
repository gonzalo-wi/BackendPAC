package com.el_jumillano.pac.integrations.aguas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AguasClientPaymentDto {

    @JsonProperty("nrocta")
    private String nrocta;

    @JsonProperty("nroctaNuevo")
    private String nroctaNuevo;

    @JsonProperty("detalle")
    private List<AguasPaymentItemDto> detalle;
}
