package com.example.tflmcpserver.model;

import jakarta.validation.constraints.Min;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "station.toilet.rate-limiter")
public record StationToiletRateLimiterProperties(@Min(1) int limitForPeriod, Duration limitRefreshPeriod,
		Duration timeoutDuration) {
}
