package com.ebious.pdf.validator.annotations;

import com.ebious.pdf.validator.validators.MatchUploadedFilesToPdf;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MatchUploadedFilesToPdf.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PdfFiles {
    String message() default "Allowed only PDF files";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
