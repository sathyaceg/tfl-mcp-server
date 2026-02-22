package com.example.tflmcpserver.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.tflmcpserver.config.TflApiProperties;
import com.example.tflmcpserver.model.JourneyPlanRequest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class TflJourneyClientTest {

    @Test
    void returnsResponseBodyForSuccessfulRequest() throws Exception {
        try (MockWebServer mockWebServer = new MockWebServer()) {
            mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\"ok\":true}"));
            mockWebServer.start();

            TflApiProperties properties = new TflApiProperties("my-key", mockWebServer.url("/").toString(), 5);
            WebClient webClient = WebClient.builder().baseUrl(properties.baseUrl()).build();
            TflJourneyClient client = new TflJourneyClient(webClient, properties);

            String response = client.journeyResults(new JourneyPlanRequest("Waterloo", "Victoria"));
            RecordedRequest recordedRequest = mockWebServer.takeRequest();

            assertEquals("{\"ok\":true}", response);
            assertTrue(recordedRequest.getPath().startsWith("/Journey/JourneyResults/Waterloo/to/Victoria"));
            assertTrue(recordedRequest.getPath().contains("app_key=my-key"));
        }
    }

    @Test
    void rejectsBlankInput() {
        TflApiProperties properties = new TflApiProperties("my-key", "https://api.tfl.gov.uk", 5);
        WebClient webClient = WebClient.builder().baseUrl(properties.baseUrl()).build();
        TflJourneyClient client = new TflJourneyClient(webClient, properties);

        assertThrows(IllegalArgumentException.class,
                () -> client.journeyResults(new JourneyPlanRequest(" ", "Victoria")));
    }

    @Test
    void throwsWhenBodyIsEmpty() throws Exception {
        try (MockWebServer mockWebServer = new MockWebServer()) {
            mockWebServer.enqueue(new MockResponse().setResponseCode(200));
            mockWebServer.start();

            TflApiProperties properties = new TflApiProperties("my-key", mockWebServer.url("/").toString(), 5);
            WebClient webClient = WebClient.builder().baseUrl(properties.baseUrl()).build();
            TflJourneyClient client = new TflJourneyClient(webClient, properties);

            assertThrows(IllegalStateException.class,
                    () -> client.journeyResults(new JourneyPlanRequest("Waterloo", "Victoria")));
        }
    }
}
