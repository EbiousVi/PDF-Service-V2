package com.ebious.pdf.validator.validators;

import com.ebious.pdf.validator.annotations.PdfFiles;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MatchUploadedFilesToPdf implements ConstraintValidator<PdfFiles, List<MultipartFile>> {

    @Override
    public void initialize(PdfFiles constraintAnnotation) {
    }

    /**
     * If PDDocument will be loaded, therefore it truly pdf file;
     */
    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext ctx) {
        ctx.disableDefaultConstraintViolation();
        if (files == null || files.isEmpty()) {
            ctx.buildConstraintViolationWithTemplate("Files is not attached or empty!").addConstraintViolation();
            return false;
        }
        for (MultipartFile file : files) {
            if (file.isEmpty() || file.isEmpty()) {
                ctx.buildConstraintViolationWithTemplate("File " + file.getOriginalFilename() +
                        " is not present! " + ctx.getDefaultConstraintMessageTemplate()).addConstraintViolation();
                return false;
            }
            if (Objects.equals(file.getContentType(), MediaType.APPLICATION_PDF_VALUE)) {
                try (PDDocument document = PDDocument.load(file.getResource().getInputStream())) {
                    return true;
                } catch (IOException e) {
                    ctx.buildConstraintViolationWithTemplate("It is not truly PDF file  < " + file.getOriginalFilename() +
                            " > Get out! " + ctx.getDefaultConstraintMessageTemplate()).addConstraintViolation();
                    return false;
                }
            }
            ctx.buildConstraintViolationWithTemplate("Attached " + file.getContentType() + ". "
                    + ctx.getDefaultConstraintMessageTemplate()).addConstraintViolation();
            return false;
        }
        return false;
    }
}

