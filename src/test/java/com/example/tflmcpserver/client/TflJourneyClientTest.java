package com.example.tflmcpserver.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.tflmcpserver.model.JourneyPlanRequest;
import com.example.tflmcpserver.model.TflApiProperties;
import com.example.tflmcpserver.model.tfl.TflItineraryResult;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class TflJourneyClientTest {

	@Test
	void returnsTypedResponseForSuccessfulRequest() throws Exception {
		try (MockWebServer mockWebServer = new MockWebServer()) {
			mockWebServer.enqueue(
					new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody(
							"{\"journeys\":[{\"duration\":12,\"startDateTime\":\"2026-02-24T10:00:00\",\"arrivalDateTime\":\"2026-02-24T10:12:00\",\"legs\":[{},{}]}]}"));
			mockWebServer.start();

			TflApiProperties properties = new TflApiProperties("my-key", mockWebServer.url("/").toString(), 5);
			WebClient webClient = WebClient.builder().baseUrl(properties.baseUrl()).build();
			TflJourneyClient client = new TflJourneyClient(webClient, properties);

			TflItineraryResult response = client.journeyResults(new JourneyPlanRequest("Waterloo", "Victoria", null));
			RecordedRequest recordedRequest = mockWebServer.takeRequest();

			assertEquals(1, response.getJourneys().size());
			assertEquals(12, response.getJourneys().get(0).getDuration());
			assertTrue(recordedRequest.getPath().startsWith("/Journey/JourneyResults/Waterloo/to/Victoria"));
			assertTrue(recordedRequest.getPath().contains("app_key=my-key"));
		}
	}

	@Test
	void parsesDisambiguationResponses() throws Exception {
		try (MockWebServer mockWebServer = new MockWebServer()) {
			mockWebServer.enqueue(
					new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody(
							"{\"$type\":\"Tfl.Api.Presentation.Entities.JourneyPlanner.DisambiguationResult, Tfl.Api.Presentation.Entities\",\"toLocationDisambiguation\":{\"disambiguationOptions\":[{\"parameterValue\":\"51.545335,-0.008048\",\"uri\":\"/journey/journeyresults/liverpool%20street/to/51.545335,-0.008048\"}]}}"));
			mockWebServer.start();

			TflApiProperties properties = new TflApiProperties("my-key", mockWebServer.url("/").toString(), 5);
			WebClient webClient = WebClient.builder().baseUrl(properties.baseUrl()).build();
			TflJourneyClient client = new TflJourneyClient(webClient, properties);

			TflItineraryResult response = client
					.journeyResults(new JourneyPlanRequest("Liverpool Street", "Foo", null));

			assertEquals(
					"Tfl.Api.Presentation.Entities.JourneyPlanner.DisambiguationResult, Tfl.Api.Presentation.Entities",
					response.getType());
			assertEquals(1, response.getToLocationDisambiguation().getDisambiguationOptions().size());
		}
	}

	@Test
	void rejectsBlankInput() {
		TflApiProperties properties = new TflApiProperties("my-key", "https://api.tfl.gov.uk", 5);
		WebClient webClient = WebClient.builder().baseUrl(properties.baseUrl()).build();
		TflJourneyClient client = new TflJourneyClient(webClient, properties);

		assertThrows(IllegalArgumentException.class,
				() -> client.journeyResults(new JourneyPlanRequest(" ", "Victoria", null)));
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
					() -> client.journeyResults(new JourneyPlanRequest("Waterloo", "Victoria", null)));
		}
	}
}
