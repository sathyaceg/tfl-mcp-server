package com.example.tflmcpserver.client;

import com.example.tflmcpserver.model.TflApiProperties;
import com.example.tflmcpserver.model.JourneyPlanRequest;
import java.time.Duration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class TflJourneyClient {

    private final WebClient tflWebClient;
    private final TflApiProperties tflApiProperties;

    public TflJourneyClient(WebClient tflWebClient, TflApiProperties tflApiProperties) {
        this.tflWebClient = tflWebClient;
        this.tflApiProperties = tflApiProperties;
    }

    public String journeyResults(JourneyPlanRequest request) {
        if (request == null || !StringUtils.hasText(request.from()) || !StringUtils.hasText(request.to())) {
            throw new IllegalArgumentException("Both 'from' and 'to' must be provided.");
        }

        return tflWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/Journey/JourneyResults/{from}/to/{to}")
                        .queryParam("app_key", tflApiProperties.key())
                        .build(request.from(), request.to()))
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(tflApiProperties.timeoutSeconds()))
                .blockOptional()
                .orElseThrow(() -> new IllegalStateException("Empty response from TfL Journey API"));
    }
}
