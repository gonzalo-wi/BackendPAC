package com.el_jumillano.pac.reconciliation.application;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record CreateReconciliationRequest(
        @NotNull Integer routeNumber,
        @NotNull @PastOrPresent LocalDate date
) {}
