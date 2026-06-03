package com.el_jumillano.pac.reconciliation.domain;

public enum ReconciliationStatus {
    PENDING,
    READY_TO_PROCESS,
    AWAITING_MANUAL_ITEMS,   // tiene cheques/retenciones esperados por Aguas que el cajero aún no cargó
    PROCESSED_WITH_SURPLUS,
    PROCESSED_WITH_SHORTAGE,
    PROCESSED_WITHOUT_DIFFERENCE,
    REQUIRES_REVIEW,
    INTEGRATION_ERROR,
    QUEUED_FOR_CLOSE,
    CLOSED
}
