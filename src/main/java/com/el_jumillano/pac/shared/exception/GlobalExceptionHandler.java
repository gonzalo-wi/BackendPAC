package com.el_jumillano.pac.shared.exception;

import com.el_jumillano.pac.reports.infrastructure.pdf.ReportGenerationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(ReconciliationAlreadyProcessedException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyProcessed(ReconciliationAlreadyProcessedException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(IntegrationUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleIntegration(IntegrationUnavailableException ex, HttpServletRequest req) {
        return build(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(RouteNotMappedException.class)
    public ResponseEntity<ErrorResponse> handleRouteNotMapped(RouteNotMappedException ex, HttpServletRequest req) {
        return build(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, details, req.getRequestURI());
    }

    @ExceptionHandler(ReportGenerationException.class)
    public ResponseEntity<ErrorResponse> handleReportGeneration(ReportGenerationException ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar el reporte PDF.", req.getRequestURI());
    }

    @ExceptionHandler(PacException.class)
    public ResponseEntity<ErrorResponse> handlePac(PacException ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor.", req.getRequestURI());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, String path) {
        return ResponseEntity.status(status).body(
                ErrorResponse.builder()
                        .status(status.value())
                        .error(status.getReasonPhrase())
                        .message(message)
                        .path(path)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
