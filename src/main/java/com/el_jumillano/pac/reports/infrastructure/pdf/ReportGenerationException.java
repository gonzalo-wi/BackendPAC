package com.el_jumillano.pac.reports.infrastructure.pdf;

import com.el_jumillano.pac.shared.exception.PacException;

public class ReportGenerationException extends PacException {

    public ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
