package com.example.pdf.exception;

public class PdfServicesException extends Exception {
    public PdfServicesException(String message) {
        super(message);
    }

    public PdfServicesException(String message, Throwable cause) {
        super(message, cause);
    }
}
