package com.ebious.pdf.validator.annotations;

import com.ebious.pdf.validator.validators.MatchUploadedFileToPdf;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MatchUploadedFileToPdf.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PdfFile {
    String message() default "Allowed only PDF file";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
