package com.modulos.excel.exception;

/**
 * Custom exception for report generation errors
 */
public class ReportGenerationException extends RuntimeException {
    public ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
