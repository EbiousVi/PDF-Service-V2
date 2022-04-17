package com.ebious.pdf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class PdfApp {

    public static void main(String[] args) {
        SpringApplication.run(PdfApp.class, args);
    }
}
