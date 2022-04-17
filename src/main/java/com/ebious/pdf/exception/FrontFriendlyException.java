package com.ebious.pdf.exception;

public class FrontFriendlyException extends RuntimeException {
    public FrontFriendlyException(String message) {
        super(message);
    }

    public FrontFriendlyException(String message, Throwable cause) {
        super(message, cause);
    }
}
