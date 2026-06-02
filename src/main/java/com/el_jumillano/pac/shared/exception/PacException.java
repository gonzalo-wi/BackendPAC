package com.el_jumillano.pac.shared.exception;

public class PacException extends RuntimeException {

    public PacException(String message) {
        super(message);
    }

    public PacException(String message, Throwable cause) {
        super(message, cause);
    }
}
