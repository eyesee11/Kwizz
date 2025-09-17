package com.example.quiz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AIConfig {

    @Bean("aiRestTemplate")
    public RestTemplate aiRestTemplate() {
        return new RestTemplate();
    }
}