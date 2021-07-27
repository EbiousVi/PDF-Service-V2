package com.example.pdf.exception;

public class CustomDBException extends Exception {
    public CustomDBException(String message) {
        super(message);
    }

    public CustomDBException(String message, Throwable cause) {
        super(message, cause);
    }
}