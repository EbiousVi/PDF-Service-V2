package com.example.pdf.anno.validators;

import com.example.pdf.anno.annotations.IsAllPDF;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ValidatorToUploadedFilesComplianceToPDF implements ConstraintValidator<IsAllPDF, List<MultipartFile>> {

    @Override
    public void initialize(IsAllPDF constraintAnnotation) {

    }

    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext ctx) {
        ctx.disableDefaultConstraintViolation();
        if (files == null || files.isEmpty()) {
            ctx.buildConstraintViolationWithTemplate("Files is not attached!")
                    .addConstraintViolation();
            return false;
        }
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                ctx.buildConstraintViolationWithTemplate("File " + file.getOriginalFilename() + " is not present! " +
                        ctx.getDefaultConstraintMessageTemplate())
                        .addConstraintViolation();
                return false;
            }
            if (Objects.equals(file.getContentType(), MediaType.APPLICATION_PDF_VALUE)) {
                try (PDDocument document = PDDocument.load(file.getResource().getInputStream())) {
                    //If document will be loaded, therefore it truly pdf file;
                } catch (IOException e) {
                    e.printStackTrace();
                    ctx.buildConstraintViolationWithTemplate("It is not truly PDF file  <" + file.getOriginalFilename() +
                            "> Get the fuck over here! " +
                            ctx.getDefaultConstraintMessageTemplate()).
                            addConstraintViolation();
                    return false;
                }
            } else {
                ctx.buildConstraintViolationWithTemplate("Attached " + file.getContentType() + ". "
                        + ctx.getDefaultConstraintMessageTemplate()).addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}

