package com.el_jumillano.pac.integrations.aguas;

import com.el_jumillano.pac.reconciliation.domain.CloseRouteResult;
import org.springframework.stereotype.Component;

@Component
public class AguasCloseResponseParser {

    public CloseRouteResult parse(String rawResponse) {
        if (rawResponse == null || rawResponse.isBlank()) {
            return CloseRouteResult.builder()
                    .success(false)
                    .rawResponse(rawResponse)
                    .errorMessage("Respuesta vacía de Aguas.")
                    .build();
        }
        boolean success = rawResponse.contains("<status>OK</status>");
        return CloseRouteResult.builder()
                .success(success)
                .rawResponse(rawResponse)
                .errorMessage(success ? null : "Aguas devolvió respuesta no OK: " + rawResponse)
                .build();
    }
}
