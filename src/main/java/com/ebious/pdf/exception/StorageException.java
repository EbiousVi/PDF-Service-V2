package com.ebious.pdf.exception;

public class StorageException extends FrontFriendlyException {
    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
