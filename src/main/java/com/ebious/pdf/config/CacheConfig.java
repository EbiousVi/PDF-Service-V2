package com.ebious.pdf.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Value("${registry.cache-size}")
    private int CACHE_SIZE;

    @Value("${registry.validity}")
    private int VALIDITY = 30;

    @Bean
    public Cache<String, String> cache() {
        return CacheBuilder.newBuilder()
                .maximumSize(CACHE_SIZE)
                .expireAfterWrite(VALIDITY, TimeUnit.MILLISECONDS)
                .build();
    }
}
