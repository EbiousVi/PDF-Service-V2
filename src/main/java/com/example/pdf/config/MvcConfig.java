package com.example.pdf.config;

import com.example.pdf.service.storage.StorageServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.management.ManagementFactory;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedOrigins("*")
                .allowedHeaders("*")
                .exposedHeaders("Content-Disposition");
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:validator-message");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }

    @Bean
    public CommandLineRunner init(StorageServiceImpl storageService) {
        return (args) -> {
            com.sun.management.OperatingSystemMXBean osMBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            System.out.println("availableProcessors:" + Runtime.getRuntime().availableProcessors());
            System.out.println("total physical memory, mb:" + osMBean.getTotalPhysicalMemorySize() / 1024 / 1024);
            System.out.println("totalMemory, mb:" + Runtime.getRuntime().totalMemory() / 1024 / 1024);
            System.out.println("maxMemory, mb:" + Runtime.getRuntime().maxMemory() / 1024 / 1024);
            System.out.println("freeMemory, mb:" + Runtime.getRuntime().freeMemory() / 1024 / 1024);
            storageService.deleteStorage();
            storageService.initStorage();
        };
    }

    @Override
    public Validator getValidator() {
        return validator();
    }


}
