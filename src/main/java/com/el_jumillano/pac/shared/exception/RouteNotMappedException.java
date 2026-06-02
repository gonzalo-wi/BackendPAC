package com.el_jumillano.pac.shared.exception;

public class RouteNotMappedException extends PacException {

    public RouteNotMappedException(Integer routeNumber) {
        super("El número de reparto " + routeNumber + " no tiene planta asignada.");
    }
}
