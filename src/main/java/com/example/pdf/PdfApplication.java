package com.example.pdf;

import com.example.pdf.service.storage.StorageServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PdfApplication extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(PdfApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(PdfApplication.class);
	}

	@Bean
	CommandLineRunner init(StorageServiceImpl storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}
}
