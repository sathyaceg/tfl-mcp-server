package com.example.tflmcpserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient tflWebClient(WebClient.Builder webClientBuilder, TflApiProperties tflApiProperties) {
        return webClientBuilder.baseUrl(tflApiProperties.baseUrl()).build();
    }
}
