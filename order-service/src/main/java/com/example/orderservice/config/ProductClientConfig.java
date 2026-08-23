package com.example.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ProductClientConfig {

    @Bean
    public RestClient productRestClient() {
        return RestClient.builder()
                .baseUrl("http://host.docker.internal:8082")
                .build();
    }
}