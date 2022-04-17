package com.ebious.pdf.exception;

public class RegistryException extends FrontFriendlyException {

    public RegistryException(String message) {
        super(message);
    }

    public RegistryException(String message, Throwable cause) {
        super(message, cause);
    }
}
