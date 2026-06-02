package com.el_jumillano.pac.shared.exception;

public class EntityNotFoundException extends PacException {

    public EntityNotFoundException(String entityType, Object id) {
        super(entityType + " con id=" + id + " no encontrado.");
    }
}
