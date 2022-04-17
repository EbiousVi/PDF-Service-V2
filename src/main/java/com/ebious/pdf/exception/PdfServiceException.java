package com.ebious.pdf.exception;

public class PdfServiceException extends FrontFriendlyException {

    public PdfServiceException(String message) {
        super(message);
    }

    public PdfServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
