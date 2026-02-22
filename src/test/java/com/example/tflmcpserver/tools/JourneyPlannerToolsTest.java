package com.example.tflmcpserver.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.tflmcpserver.client.TflJourneyClient;
import com.example.tflmcpserver.config.TflApiProperties;
import com.example.tflmcpserver.service.JourneyPlannerService;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class JourneyPlannerToolsTest {

    @Test
    void delegatesToService() {
        TrackingJourneyService service = new TrackingJourneyService();
        JourneyPlannerTools journeyPlannerTools = new JourneyPlannerTools(service);

        String response = journeyPlannerTools.planJourney("From", "To");

        assertEquals("tool-result", response);
        assertEquals("From", service.capturedFrom);
        assertEquals("To", service.capturedTo);
    }

    private static class TrackingJourneyService extends JourneyPlannerService {

        private String capturedFrom;
        private String capturedTo;

        TrackingJourneyService() {
            super(new TflJourneyClient(WebClient.builder().build(),
                    new TflApiProperties("key", "https://api.tfl.gov.uk", 5)));
        }

        @Override
        public String planJourney(String from, String to) {
            this.capturedFrom = from;
            this.capturedTo = to;
            return "tool-result";
        }
    }
}
