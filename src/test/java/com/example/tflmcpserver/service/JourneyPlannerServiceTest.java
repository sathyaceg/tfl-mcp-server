package com.example.tflmcpserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.tflmcpserver.client.TflJourneyClient;
import com.example.tflmcpserver.model.JourneyPlanRequest;
import com.example.tflmcpserver.model.JourneyPlanToolResponse;
import com.example.tflmcpserver.model.JourneyPlannerErrorCode;
import com.example.tflmcpserver.model.tfl.TflDisambiguation;
import com.example.tflmcpserver.model.tfl.TflDisambiguationOption;
import com.example.tflmcpserver.model.tfl.TflDisruption;
import com.example.tflmcpserver.model.tfl.TflIdentifier;
import com.example.tflmcpserver.model.tfl.TflItineraryResult;
import com.example.tflmcpserver.model.tfl.TflInstruction;
import com.example.tflmcpserver.model.tfl.TflJourney;
import com.example.tflmcpserver.model.tfl.TflJourneyFare;
import com.example.tflmcpserver.model.tfl.TflJourneyLeg;
import com.example.tflmcpserver.model.tfl.TflPath;
import com.example.tflmcpserver.model.tfl.TflPlannedWork;
import com.example.tflmcpserver.model.tfl.TflRouteOption;
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
	private RateLimiter journeyPlannerRateLimiter;

	@InjectMocks
	private JourneyPlannerService journeyPlannerService;

	@Test
	void returnsTopFiveFastestJourneysSortedByDuration() {
		JourneyPlanRequest request = new JourneyPlanRequest("A", "B", null);
		when(journeyPlannerRateLimiter.acquirePermission()).thenReturn(true);
		when(tflJourneyClient.journeyResults(request)).thenReturn(new TflItineraryResult(
				List.of(new TflJourney(30, "2026-02-24T10:00:00", "2026-02-24T10:30:00", List.of()),
						new TflJourney(10, "2026-02-24T10:00:00", "2026-02-24T10:10:00", List.of()),
						new TflJourney(15, "2026-02-24T10:00:00", "2026-02-24T10:15:00", List.of()),
						new TflJourney(8, "2026-02-24T10:00:00", "2026-02-24T10:08:00", List.of()),
						new TflJourney(18, "2026-02-24T10:00:00", "2026-02-24T10:18:00", List.of()),
						new TflJourney(9, "2026-02-24T10:00:00", "2026-02-24T10:09:00", List.of()))));

		JourneyPlanToolResponse response = journeyPlannerService.planJourney(request);

		assertEquals(true, response.success());
		assertEquals("OK", response.code());
		assertEquals(5, response.topJourneyDetails().size());
		assertEquals(8, response.topJourneyDetails().get(0).durationMinutes());
		assertEquals(9, response.topJourneyDetails().get(1).durationMinutes());
		assertEquals(10, response.topJourneyDetails().get(2).durationMinutes());
	}

	@Test
	void returnsDisambiguationRequiredWithTopSuggestions() {
		JourneyPlanRequest request = new JourneyPlanRequest("Liverpool Street", "Foo", null);
		when(journeyPlannerRateLimiter.acquirePermission()).thenReturn(true);
		TflItineraryResult disambiguationResult = new TflItineraryResult();
		disambiguationResult.setToLocationDisambiguation(
				new TflDisambiguation(List.of(new TflDisambiguationOption(80, "51.545335,-0.008048", "/uri1"),
						new TflDisambiguationOption(95, "51.520000,-0.010000", "/uri2"))));
		when(tflJourneyClient.journeyResults(request)).thenReturn(disambiguationResult);

		JourneyPlanToolResponse response = journeyPlannerService.planJourney(request);

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
	void mapsDetailedJourneyAndLegFields() {
		JourneyPlanRequest request = new JourneyPlanRequest("A", "B", null);
		when(journeyPlannerRateLimiter.acquirePermission()).thenReturn(true);

		TflInstruction instruction = new TflInstruction();
		instruction.setSummary("Central line to Tottenham Court Road");
		instruction.setDetailed("Central line towards Ealing Broadway");

		TflIdentifier mode = new TflIdentifier();
		mode.setId("tube");
		mode.setName("tube");

		TflIdentifier line = new TflIdentifier();
		line.setName("Central");

		TflRouteOption routeOption = new TflRouteOption();
		routeOption.setName("IgnoreMeWhenLineIdentifierExists");
		routeOption.setDirection("Inbound");
		routeOption.setLineIdentifier(line);

		TflDisruption disruption = new TflDisruption();
		disruption.setSummary("Central Line: Minor delays.");

		TflPlannedWork plannedWork = new TflPlannedWork();
		plannedWork.setDescription("Planned escalator maintenance.");

		TflIdentifier stop1 = new TflIdentifier();
		stop1.setName("Bank Underground Station");
		TflIdentifier stop2 = new TflIdentifier();
		stop2.setId("940GZZLUTCR");
		TflPath path = new TflPath();
		path.setStopPoints(List.of(stop1, stop2));

		TflJourneyLeg leg = new TflJourneyLeg();
		leg.setDuration(11);
		leg.setDepartureTime("2026-03-05T23:39:00");
		leg.setArrivalTime("2026-03-05T23:50:00");
		leg.setDistance(1200.5);
		leg.setInstruction(instruction);
		leg.setMode(mode);
		leg.setRouteOptions(List.of(routeOption));
		leg.setInterChangeDuration("6");
		leg.setInterChangePosition("AFTER");
		leg.setDisrupted(true);
		leg.setDisruptions(List.of(disruption));
		leg.setPlannedWorks(List.of(plannedWork));
		leg.setPath(path);

		TflJourneyFare fare = new TflJourneyFare();
		fare.setTotalCost(310);

		TflJourney journey = new TflJourney(18, "2026-03-05T23:39:00", "2026-03-05T23:57:00", List.of(leg));
		journey.setDescription("Test description");
		journey.setAlternativeRoute(false);
		journey.setFare(fare);

		when(tflJourneyClient.journeyResults(request)).thenReturn(new TflItineraryResult(List.of(journey)));

		JourneyPlanToolResponse response = journeyPlannerService.planJourney(request);

		assertEquals(true, response.success());
		assertEquals(1, response.topJourneyDetails().size());
		assertEquals(18, response.topJourneyDetails().get(0).durationMinutes());
		assertEquals("Test description", response.topJourneyDetails().get(0).description());
		assertEquals(310, response.topJourneyDetails().get(0).totalFarePence());
		assertEquals(1, response.topJourneyDetails().get(0).legs().size());
		assertEquals("Central line to Tottenham Court Road",
				response.topJourneyDetails().get(0).legs().get(0).instructionSummary());
		assertEquals("Central line towards Ealing Broadway",
				response.topJourneyDetails().get(0).legs().get(0).instructionDetailed());
		assertEquals("tube", response.topJourneyDetails().get(0).legs().get(0).modeId());
		assertEquals("tube", response.topJourneyDetails().get(0).legs().get(0).modeName());
		assertEquals("Central", response.topJourneyDetails().get(0).legs().get(0).routeName());
		assertEquals("Inbound", response.topJourneyDetails().get(0).legs().get(0).direction());
		assertEquals("6", response.topJourneyDetails().get(0).legs().get(0).interChangeDuration());
		assertEquals("AFTER", response.topJourneyDetails().get(0).legs().get(0).interChangePosition());
		assertEquals(true, response.topJourneyDetails().get(0).legs().get(0).disrupted());
		assertEquals(List.of("Central Line: Minor delays."),
				response.topJourneyDetails().get(0).legs().get(0).disruptionSummaries());
		assertEquals(List.of("Planned escalator maintenance."),
				response.topJourneyDetails().get(0).legs().get(0).plannedWorkDescriptions());
		assertEquals(List.of("Bank Underground Station", "940GZZLUTCR"),
				response.topJourneyDetails().get(0).legs().get(0).stopPoints());
	}

	@Test
	void fallsBackWhenOptionalRouteAndDisruptionFieldsAreMissing() {
		JourneyPlanRequest request = new JourneyPlanRequest("A", "B", null);
		when(journeyPlannerRateLimiter.acquirePermission()).thenReturn(true);

		TflDisruption disruption = new TflDisruption();
		disruption.setDescription("Use alternative transport.");

		TflRouteOption routeOption = new TflRouteOption();
		routeOption.setName("Northern");
		routeOption.setDirection("Inbound");

		TflJourneyLeg leg = new TflJourneyLeg();
		leg.setDuration(2);
		leg.setRouteOptions(List.of(routeOption));
		leg.setDisruptions(List.of(disruption));

		TflJourney journey = new TflJourney(2, "2026-03-05T23:39:00", "2026-03-05T23:41:00", List.of(leg));
		when(tflJourneyClient.journeyResults(request)).thenReturn(new TflItineraryResult(List.of(journey)));

		JourneyPlanToolResponse response = journeyPlannerService.planJourney(request);

		assertEquals("Northern", response.topJourneyDetails().get(0).legs().get(0).routeName());
		assertEquals(List.of("Use alternative transport."),
				response.topJourneyDetails().get(0).legs().get(0).disruptionSummaries());
	}

	@Test
	void mapsValidationErrors() {
		JourneyPlanRequest request = new JourneyPlanRequest("", "B", null);
		when(journeyPlannerRateLimiter.acquirePermission()).thenReturn(true);
		when(tflJourneyClient.journeyResults(request)).thenThrow(new IllegalArgumentException("invalid input"));

		JourneyPlanToolResponse response = journeyPlannerService.planJourney(request);

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

		JourneyPlanToolResponse response = journeyPlannerService.planJourney(request);

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

		JourneyPlanToolResponse response = journeyPlannerService.planJourney(request);

		assertEquals(false, response.success());
		assertEquals(JourneyPlannerErrorCode.UPSTREAM_TIMEOUT.name(), response.code());
	}

	@Test
	void mapsUnexpectedErrors() {
		JourneyPlanRequest request = new JourneyPlanRequest("A", "B", null);
		when(journeyPlannerRateLimiter.acquirePermission()).thenReturn(true);
		when(tflJourneyClient.journeyResults(request)).thenThrow(new RuntimeException("boom"));

		JourneyPlanToolResponse response = journeyPlannerService.planJourney(request);

		assertEquals(false, response.success());
		assertEquals(JourneyPlannerErrorCode.INTERNAL_ERROR.name(), response.code());
	}

	@Test
	void returnsRateLimitErrorWhenNoPermission() {
		JourneyPlanRequest request = new JourneyPlanRequest("A", "B", null);
		when(journeyPlannerRateLimiter.acquirePermission()).thenReturn(false);

		JourneyPlanToolResponse response = journeyPlannerService.planJourney(request);

		assertEquals(false, response.success());
		assertEquals(JourneyPlannerErrorCode.RATE_LIMIT_EXCEEDED.name(), response.code());
		verifyNoInteractions(tflJourneyClient);
	}
}
