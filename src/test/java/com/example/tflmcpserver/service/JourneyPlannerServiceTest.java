package com.example.tflmcpserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.example.tflmcpserver.client.TflJourneyClient;
import com.example.tflmcpserver.model.JourneyPlanRequest;
import com.example.tflmcpserver.model.JourneyPlanToolResponse;
import com.example.tflmcpserver.model.JourneyPlannerErrorCode;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@ExtendWith(MockitoExtension.class)
class JourneyPlannerServiceTest {

    @Mock
    private TflJourneyClient tflJourneyClient;

    @InjectMocks
    private JourneyPlannerService journeyPlannerService;

    @Test
    void returnsSuccessResponseWhenClientSucceeds() {
        JourneyPlanRequest request = new JourneyPlanRequest("A", "B", null);
        when(tflJourneyClient.journeyResults(request)).thenReturn("{\"journey\":\"ok\"}");

        JourneyPlanToolResponse response = journeyPlannerService.planJourney(request);

        assertEquals(true, response.success());
        assertEquals("OK", response.code());
        assertEquals("{\"journey\":\"ok\"}", response.journeyJson());
    }

    @Test
    void mapsValidationErrors() {
        JourneyPlanRequest request = new JourneyPlanRequest("", "B", null);
        when(tflJourneyClient.journeyResults(request)).thenThrow(new IllegalArgumentException("invalid input"));

        JourneyPlanToolResponse response = journeyPlannerService.planJourney(request);

        assertEquals(false, response.success());
        assertEquals(JourneyPlannerErrorCode.VALIDATION_ERROR.name(), response.code());
        assertEquals("invalid input", response.message());
    }

    @Test
    void mapsUpstreamHttpErrors() {
        JourneyPlanRequest request = new JourneyPlanRequest("A", "B", null);
        WebClientResponseException exception = WebClientResponseException.create(
                HttpStatus.BAD_GATEWAY.value(),
                "Bad Gateway",
                null,
                new byte[0],
                null);
        when(tflJourneyClient.journeyResults(request)).thenThrow(exception);

        JourneyPlanToolResponse response = journeyPlannerService.planJourney(request);

        assertEquals(false, response.success());
        assertEquals(JourneyPlannerErrorCode.UPSTREAM_ERROR.name(), response.code());
    }

    @Test
    void mapsTimeoutsFromRequestExceptions() {
        JourneyPlanRequest request = new JourneyPlanRequest("A", "B", null);
        WebClientRequestException exception = new WebClientRequestException(
                new TimeoutException("timeout"),
                HttpMethod.GET,
                java.net.URI.create("https://api.tfl.gov.uk/Journey/JourneyResults/A/to/B"),
                HttpHeaders.EMPTY);
        when(tflJourneyClient.journeyResults(request)).thenThrow(exception);

        JourneyPlanToolResponse response = journeyPlannerService.planJourney(request);

        assertEquals(false, response.success());
        assertEquals(JourneyPlannerErrorCode.UPSTREAM_TIMEOUT.name(), response.code());
    }

    @Test
    void mapsUnexpectedErrors() {
        JourneyPlanRequest request = new JourneyPlanRequest("A", "B", null);
        when(tflJourneyClient.journeyResults(request)).thenThrow(new RuntimeException("boom"));

        JourneyPlanToolResponse response = journeyPlannerService.planJourney(request);

        assertEquals(false, response.success());
        assertEquals(JourneyPlannerErrorCode.INTERNAL_ERROR.name(), response.code());
    }
}
