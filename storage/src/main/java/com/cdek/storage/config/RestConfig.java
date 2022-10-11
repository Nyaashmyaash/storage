package com.cdek.storage.config;

import com.cdek.exceptions.web.client.DefaultResponseRestTemplateErrorHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {

    @Bean
    @Primary
    public RestTemplate restTemplate(RestTemplateBuilder builder, ObjectMapper objectMapper) {
        var restTemplate = builder.build();
        restTemplate.setErrorHandler(new DefaultResponseRestTemplateErrorHandler(objectMapper));
        return restTemplate;
    }
}
