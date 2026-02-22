package com.example.tflmcpserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.tflmcpserver.client.TflJourneyClient;
import com.example.tflmcpserver.config.TflApiProperties;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class JourneyPlannerServiceTest {

    @Test
    void delegatesToClient() {
        TrackingJourneyClient client = new TrackingJourneyClient();
        JourneyPlannerService journeyPlannerService = new JourneyPlannerService(client);

        String response = journeyPlannerService.planJourney("A", "B");

        assertEquals("result", response);
        assertEquals("A", client.capturedFrom);
        assertEquals("B", client.capturedTo);
    }

    private static class TrackingJourneyClient extends TflJourneyClient {

        private String capturedFrom;
        private String capturedTo;

        TrackingJourneyClient() {
            super(WebClient.builder().build(), new TflApiProperties("key", "https://api.tfl.gov.uk", 5));
        }

        @Override
        public String journeyResults(String from, String to) {
            this.capturedFrom = from;
            this.capturedTo = to;
            return "result";
        }
    }
}
