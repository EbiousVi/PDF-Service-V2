package com.ebious.pdf.validator.validators;

import com.ebious.pdf.validator.annotations.PdfFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.util.Objects;

public class MatchUploadedFileToPdf implements ConstraintValidator<PdfFile, MultipartFile> {

    @Override
    public void initialize(PdfFile constraintAnnotation) {
    }

    /**
     * If PDDocument will be loaded, therefore it truly pdf file;
     */
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext ctx) {
        ctx.disableDefaultConstraintViolation();
        if (file == null || file.isEmpty()) {
            ctx.buildConstraintViolationWithTemplate("File is not attached or empty!").addConstraintViolation();
            return false;
        }
        if (Objects.equals(file.getContentType(), MediaType.APPLICATION_PDF_VALUE)) {
            try (PDDocument document = PDDocument.load(file.getResource().getInputStream())) {
                return true;
            } catch (IOException e) {
                String expMsg = "It is not truly PDF file < " + file.getOriginalFilename() +
                        " > Get out! " + ctx.getDefaultConstraintMessageTemplate();
                ctx.buildConstraintViolationWithTemplate(expMsg).addConstraintViolation();
                return false;
            }
        }
        String message = "Attached " + file.getContentType() + ". " + ctx.getDefaultConstraintMessageTemplate();
        ctx.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }
}
