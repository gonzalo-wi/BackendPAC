package com.el_jumillano.pac.shared.exception;

public class IntegrationUnavailableException extends PacException {

    public IntegrationUnavailableException(String provider, Throwable cause) {
        super("El proveedor de integración '" + provider + "' no está disponible.", cause);
    }

    public IntegrationUnavailableException(String provider, String detail) {
        super("El proveedor de integración '" + provider + "' no está disponible: " + detail);
    }
}
