package com.ebious.pdf.exception;

public class ZipServiceException extends FrontFriendlyException {

    public ZipServiceException(String message) {
        super(message);
    }

    public ZipServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
