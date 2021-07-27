package com.example.pdf.anno.annotations;

import com.example.pdf.anno.validators.ValidatorToUploadedFileComplianceToPDF;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidatorToUploadedFileComplianceToPDF.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsPDF {
    String message() default "Allowed only PDF file";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
