package com.example.tflmcpserver.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class WebClientConfigTest {

    @Test
    void buildsWebClientWithConfiguredBaseUrl() throws Exception {
        try (MockWebServer mockWebServer = new MockWebServer()) {
            mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("ok"));
            mockWebServer.start();

            TflApiProperties properties = new TflApiProperties("key", mockWebServer.url("/").toString(), 5);
            WebClientConfig config = new WebClientConfig();
            WebClient webClient = config.tflWebClient(WebClient.builder(), properties);

            String body = webClient.get()
                    .uri("/ping")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            RecordedRequest request = mockWebServer.takeRequest();
            assertEquals("ok", body);
            assertEquals("/ping", request.getPath());
        }
    }
}
