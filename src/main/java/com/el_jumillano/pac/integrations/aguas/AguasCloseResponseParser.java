package com.el_jumillano.pac.integrations.aguas;

import com.el_jumillano.pac.reconciliation.domain.CloseRouteResult;
import org.springframework.stereotype.Component;

@Component
public class AguasCloseResponseParser {

    /**
     * Aguas devuelve HTTP 200 con body vacío en caso de éxito.
     * Si el HTTP status no fue 200 Feign ya lanzó excepción antes de llegar acá.
     */
    public CloseRouteResult parseSuccess(String rawResponse) {
        return CloseRouteResult.builder()
                .success(true)
                .rawResponse(rawResponse)
                .build();
    }

    public CloseRouteResult parseError(String errorMessage) {
        return CloseRouteResult.builder()
                .success(false)
                .errorMessage(errorMessage)
                .build();
    }
}
