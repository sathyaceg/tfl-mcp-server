package com.example.tflmcpserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.tflmcpserver.client.TflJourneyClient;
import com.example.tflmcpserver.model.JourneyPlannerErrorCode;
import com.example.tflmcpserver.model.api.journey.JourneyDisambiguationSuggestion;
import com.example.tflmcpserver.model.api.journey.JourneyOptionDetail;
import com.example.tflmcpserver.model.api.journey.JourneyPlanRequest;
import com.example.tflmcpserver.model.api.journey.JourneyPlanResponse;
import com.example.tflmcpserver.model.tfl.journey.TflDisambiguationWire;
import com.example.tflmcpserver.model.tfl.journey.TflDisambiguationOptionWire;
import com.example.tflmcpserver.model.tfl.journey.TflItineraryResultWire;
import com.example.tflmcpserver.model.tfl.journey.TflJourneyWire;
import com.example.tflmcpserver.mapper.JourneyResponseMapper;
import io.github.resilience4j.ratelimiter.RateLimiter;
import java.util.List;
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

	@Mock
	private JourneyResponseMapper journeyResponseMapper;

	@Mock
	private RateLimiter journeyPlannerRateLimiter;

	@InjectMocks
	private JourneyPlannerService journeyPlannerService;

	@Test
	void returnsTopFiveFastestJourneysSortedByDuration() {
		JourneyPlanRequest request = new JourneyPlanRequest("A", "B", null);
		when(journeyPlannerRateLimiter.acquirePermission()).thenReturn(true);
		when(tflJourneyClient.journeyResults(request)).thenReturn(new TflItineraryResultWire(
				List.of(new TflJourneyWire(30, "2026-02-24T10:00:00", "2026-02-24T10:30:00", List.of()))));
		when(journeyResponseMapper.toDisambiguationSuggestions(any())).thenReturn(List.of(), List.of());
		when(journeyResponseMapper.toTopJourneyDetails(any(), eq(5))).thenReturn(List.of(new JourneyOptionDetail(8,
				"2026-02-24T10:00:00", "2026-02-24T10:08:00", 1, null, false, null, List.of())));

		JourneyPlanResponse response = journeyPlannerService.planJourney(request);

		assertEquals(true, response.success());
		assertEquals("OK", response.code());
		assertEquals(1, response.topJourneyDetails().size());
		assertEquals(8, response.topJourneyDetails().get(0).durationMinutes());
	}

	@Test
	void returnsDisambiguationRequiredWithTopSuggestions() {
		JourneyPlanRequest request = new JourneyPlanRequest("Liverpool Street", "Foo", null);
		when(journeyPlannerRateLimiter.acquirePermission()).thenReturn(true);
		TflItineraryResultWire disambiguationResult = new TflItineraryResultWire();
		disambiguationResult.setToLocationDisambiguation(
				new TflDisambiguationWire(List.of(new TflDisambiguationOptionWire(80, "51.545335,-0.008048", "/uri1"),
						new TflDisambiguationOptionWire(95, "51.520000,-0.010000", "/uri2"))));
		when(tflJourneyClient.journeyResults(request)).thenReturn(disambiguationResult);
		when(journeyResponseMapper.toDisambiguationSuggestions(any())).thenReturn(List.of(),
				List.of(new JourneyDisambiguationSuggestion("51.520000,-0.010000", 95),
						new JourneyDisambiguationSuggestion("51.545335,-0.008048", 80)));

		JourneyPlanResponse response = journeyPlannerService.planJourney(request);

		assertEquals(false, response.success());
		assertEquals(JourneyPlannerErrorCode.DISAMBIGUATION_REQUIRED.name(), response.code());
		assertEquals(0, response.fromLocationDisambiguation().size());
		assertEquals(2, response.toLocationDisambiguation().size());
		assertEquals("51.520000,-0.010000", response.toLocationDisambiguation().get(0).parameterValue());
		assertEquals(95, response.toLocationDisambiguation().get(0).matchQuality());
		assertEquals("51.545335,-0.008048", response.toLocationDisambiguation().get(1).parameterValue());
		assertEquals(80, response.toLocationDisambiguation().get(1).matchQuality());
	}

	@Test
	void mapsValidationErrors() {
		JourneyPlanRequest request = new JourneyPlanRequest("", "B", null);
		when(journeyPlannerRateLimiter.acquirePermission()).thenReturn(true);
		when(tflJourneyClient.journeyResults(request)).thenThrow(new IllegalArgumentException("invalid input"));

		JourneyPlanResponse response = journeyPlannerService.planJourney(request);

		assertEquals(false, response.success());
		assertEquals(JourneyPlannerErrorCode.VALIDATION_ERROR.name(), response.code());
		assertEquals("invalid input", response.message());
	}

	@Test
	void mapsUpstreamHttpErrors() {
		JourneyPlanRequest request = new JourneyPlanRequest("A", "B", null);
		when(journeyPlannerRateLimiter.acquirePermission()).thenReturn(true);
		WebClientResponseException exception = WebClientResponseException.create(HttpStatus.BAD_GATEWAY.value(),
				"Bad Gateway", null, new byte[0], null);
		when(tflJourneyClient.journeyResults(request)).thenThrow(exception);

		JourneyPlanResponse response = journeyPlannerService.planJourney(request);

		assertEquals(false, response.success());
		assertEquals(JourneyPlannerErrorCode.UPSTREAM_ERROR.name(), response.code());
	}

	@Test
	void mapsTimeoutsFromRequestExceptions() {
		JourneyPlanRequest request = new JourneyPlanRequest("A", "B", null);
		when(journeyPlannerRateLimiter.acquirePermission()).thenReturn(true);
		WebClientRequestException exception = new WebClientRequestException(new TimeoutException("timeout"),
				HttpMethod.GET, java.net.URI.create("https://api.tfl.gov.uk/Journey/JourneyResults/A/to/B"),
				HttpHeaders.EMPTY);
		when(tflJourneyClient.journeyResults(request)).thenThrow(exception);

		JourneyPlanResponse response = journeyPlannerService.planJourney(request);

		assertEquals(false, response.success());
		assertEquals(JourneyPlannerErrorCode.UPSTREAM_TIMEOUT.name(), response.code());
	}

	@Test
	void mapsUnexpectedErrors() {
		JourneyPlanRequest request = new JourneyPlanRequest("A", "B", null);
		when(journeyPlannerRateLimiter.acquirePermission()).thenReturn(true);
		when(tflJourneyClient.journeyResults(request)).thenThrow(new RuntimeException("boom"));

		JourneyPlanResponse response = journeyPlannerService.planJourney(request);

		assertEquals(false, response.success());
		assertEquals(JourneyPlannerErrorCode.INTERNAL_ERROR.name(), response.code());
	}

	@Test
	void returnsRateLimitErrorWhenNoPermission() {
		JourneyPlanRequest request = new JourneyPlanRequest("A", "B", null);
		when(journeyPlannerRateLimiter.acquirePermission()).thenReturn(false);

		JourneyPlanResponse response = journeyPlannerService.planJourney(request);

		assertEquals(false, response.success());
		assertEquals(JourneyPlannerErrorCode.RATE_LIMIT_EXCEEDED.name(), response.code());
		verifyNoInteractions(tflJourneyClient);
	}
}
