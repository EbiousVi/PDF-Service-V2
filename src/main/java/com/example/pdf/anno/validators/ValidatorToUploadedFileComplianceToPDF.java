package com.example.pdf.anno.validators;

import com.example.pdf.anno.annotations.IsPDF;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.util.Objects;

public class ValidatorToUploadedFileComplianceToPDF implements ConstraintValidator<IsPDF, MultipartFile> {

    @Override
    public void initialize(IsPDF constraintAnnotation) {

    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext ctx) {
        ctx.disableDefaultConstraintViolation();
        if (file == null || file.isEmpty()) {
            ctx.buildConstraintViolationWithTemplate("File is not attached!").addConstraintViolation();
            return false;
        }
        if (Objects.equals(file.getContentType(), MediaType.APPLICATION_PDF_VALUE)) {
            try (PDDocument document = PDDocument.load(file.getResource().getInputStream())) {
                //If document will be loaded, therefore it truly pdf file;
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                ctx.buildConstraintViolationWithTemplate("It is not truly PDF file <" + file.getOriginalFilename() +
                        "> Get the fuck over here! " +
                        ctx.getDefaultConstraintMessageTemplate()).
                        addConstraintViolation();
                return false;
            }
        }
        ctx.buildConstraintViolationWithTemplate("Attached " + file.getContentType() + ". " +
                ctx.getDefaultConstraintMessageTemplate()).addConstraintViolation();
        return false;
    }
}
