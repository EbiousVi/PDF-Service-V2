package com.ebious.pdf.controller.exception;

import com.ebious.pdf.exception.RegistryException;
import com.ebious.pdf.exception.NamingServiceException;
import com.ebious.pdf.exception.PdfServiceException;
import com.ebious.pdf.exception.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Iterator;

@ControllerAdvice
public class ErrorHandlingController {

    private final static Logger logger = LoggerFactory.getLogger(ErrorHandlingController.class);

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String onConstraintValidationException(ConstraintViolationException ex) {
        logger.info("Constraint Violation Exception!", ex);
        Iterator<ConstraintViolation<?>> iterator = ex.getConstraintViolations().iterator();
        StringBuilder message = new StringBuilder();
        while (iterator.hasNext()) {
            message.append(iterator.next().getMessage()).append(System.lineSeparator());
        }
        message.deleteCharAt(message.lastIndexOf(System.lineSeparator()));
        return message.toString();
    }

    @ExceptionHandler(StorageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String onStorageException(StorageException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(RegistryException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String onRegistryException(RegistryException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(PdfServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String onPdfServicesException(PdfServiceException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(NamingServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String onNamingServiceException(NamingServiceException ex) {
        return ex.getMessage();
    }
}
