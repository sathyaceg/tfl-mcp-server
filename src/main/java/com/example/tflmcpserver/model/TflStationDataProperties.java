package com.example.tflmcpserver.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "tfl.station-data")
public record TflStationDataProperties(@NotBlank String detailedDir) {
}
