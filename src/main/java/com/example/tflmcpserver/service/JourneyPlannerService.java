package com.example.tflmcpserver.service;

import com.example.tflmcpserver.client.TflJourneyClient;
import com.example.tflmcpserver.model.JourneyPlanRequest;
import com.example.tflmcpserver.model.JourneyPlanToolResponse;
import com.example.tflmcpserver.model.JourneyPlannerErrorCode;
import java.util.concurrent.TimeoutException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class JourneyPlannerService {

    private static final String TIMEOUT_MESSAGE = "Timed out while calling TfL API.";
    private static final String UPSTREAM_REQUEST_FAILED_MESSAGE = "TfL API request failed.";
    private static final String INTERNAL_ERROR_MESSAGE = "Unexpected error while planning journey.";

    private final TflJourneyClient tflJourneyClient;

    public JourneyPlannerService(TflJourneyClient tflJourneyClient) {
        this.tflJourneyClient = tflJourneyClient;
    }

    public JourneyPlanToolResponse planJourney(JourneyPlanRequest request) {
        try {
            String journeyJson = tflJourneyClient.journeyResults(request);
            return JourneyPlanToolResponse.success(journeyJson);
        } catch (RuntimeException ex) {
            return toErrorResponse(ex);
        }
    }

    private JourneyPlanToolResponse toErrorResponse(RuntimeException ex) {
        if (ex instanceof IllegalArgumentException) {
            return error(JourneyPlannerErrorCode.VALIDATION_ERROR, ex.getMessage());
        }
        if (ex instanceof WebClientResponseException responseException) {
            return error(
                    JourneyPlannerErrorCode.UPSTREAM_ERROR,
                    "TfL API returned status " + responseException.getStatusCode().value() + ".");
        }
        if (ex instanceof WebClientRequestException requestException) {
            return hasTimeoutCause(requestException)
                    ? error(JourneyPlannerErrorCode.UPSTREAM_TIMEOUT, TIMEOUT_MESSAGE)
                    : error(JourneyPlannerErrorCode.UPSTREAM_ERROR, UPSTREAM_REQUEST_FAILED_MESSAGE);
        }
        return hasTimeoutCause(ex)
                ? error(JourneyPlannerErrorCode.UPSTREAM_TIMEOUT, TIMEOUT_MESSAGE)
                : error(JourneyPlannerErrorCode.INTERNAL_ERROR, INTERNAL_ERROR_MESSAGE);
    }

    private JourneyPlanToolResponse error(JourneyPlannerErrorCode code, String message) {
        return JourneyPlanToolResponse.error(code.name(), message);
    }

    private boolean hasTimeoutCause(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof TimeoutException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
