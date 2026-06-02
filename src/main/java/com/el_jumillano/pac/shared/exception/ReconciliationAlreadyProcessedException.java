package com.el_jumillano.pac.shared.exception;

public class ReconciliationAlreadyProcessedException extends PacException {

    public ReconciliationAlreadyProcessedException(Long reconciliationId) {
        super("La conciliación con id=" + reconciliationId + " ya fue procesada o cerrada.");
    }
}
