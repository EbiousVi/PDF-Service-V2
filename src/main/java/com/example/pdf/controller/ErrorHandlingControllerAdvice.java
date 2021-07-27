package com.example.pdf.controller;

import com.example.pdf.exception.CustomDBException;
import com.example.pdf.exception.PdfServiceException;
import com.example.pdf.exception.StorageException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class ErrorHandlingControllerAdvice {
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String onConstraintValidationException(ConstraintViolationException ex) {
        return ex.getConstraintViolations().iterator().next().getMessage();
    }

    @ExceptionHandler(StorageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String onStorageException(StorageException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(PdfServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String onPdfServicesException(PdfServiceException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(CustomDBException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String onCustomDBException(CustomDBException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(OutOfMemoryError.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String onOutOfMemoryError(OutOfMemoryError ex) {
        ex.printStackTrace();
        return "OutOfMemoryError! Only Creator can help! " + ex.getMessage();
    }

    @ExceptionHandler(StackOverflowError.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String onStackOverflowError(StackOverflowError ex) {
        ex.printStackTrace();
        return "StackOverflowError! Only Creator can help! " + ex.getMessage();
    }

    @ExceptionHandler(Error.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String onOtherError(Error ex) {
        ex.printStackTrace();
        return "Only Creator can help!";
    }
}
