package com.el_jumillano.pac.integrations.aguas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * Wrapper para la respuesta OData de Aguas:
 * { "value": [...], "Count": 1 }
 */
@Data
@NoArgsConstructor
public class AguasExpectedResponse {

    @JsonProperty("value")
    private List<AguasExpectedDto> value = Collections.emptyList();

    @JsonProperty("Count")
    private Integer count;
}
