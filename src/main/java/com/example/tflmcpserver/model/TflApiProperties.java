package com.example.tflmcpserver.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "tfl.api")
public record TflApiProperties(@NotBlank String key, @NotBlank String baseUrl, @Min(1) @Max(60) int timeoutSeconds) {
}
