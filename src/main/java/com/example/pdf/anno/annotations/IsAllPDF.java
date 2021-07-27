package com.example.pdf.anno.annotations;

import com.example.pdf.anno.validators.ValidatorToUploadedFilesComplianceToPDF;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidatorToUploadedFilesComplianceToPDF.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsAllPDF {
    String message() default "Allowed only PDF files";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
