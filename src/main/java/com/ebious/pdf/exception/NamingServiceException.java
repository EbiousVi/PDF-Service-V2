package com.ebious.pdf.exception;

public class NamingServiceException extends FrontFriendlyException {

    public NamingServiceException(String message) {
        super(message);
    }

    public NamingServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}