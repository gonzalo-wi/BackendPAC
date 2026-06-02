package com.el_jumillano.pac.reconciliation.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloseRouteResult {

    private boolean success;
    private String rawResponse;
    private String errorMessage;
}
