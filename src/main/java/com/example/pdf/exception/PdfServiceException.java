package com.example.pdf.exception;

public class PdfServiceException extends Exception {
    public PdfServiceException(String message) {
        super(message);
    }

    public PdfServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
