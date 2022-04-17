package com.ebious.pdf.validator.validators;

import com.ebious.pdf.service.storage.registry.proxy.RegistryProxyCache;
import com.ebious.pdf.validator.annotations.RegistryId;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RegistryIdValidation implements ConstraintValidator<RegistryId, String> {

    private final RegistryProxyCache registryProxyCache;

    @Autowired
    public RegistryIdValidation(RegistryProxyCache registryProxyCache) {
        this.registryProxyCache = registryProxyCache;
    }

    @Override
    public boolean isValid(String registryId, ConstraintValidatorContext ctx) {
        ctx.disableDefaultConstraintViolation();
        registryProxyCache.getEntry(registryId);
        return true;
    }

    @Override
    public void initialize(RegistryId constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
