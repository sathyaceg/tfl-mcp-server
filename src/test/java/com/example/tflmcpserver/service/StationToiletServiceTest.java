package com.example.tflmcpserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.tflmcpserver.client.TflStationDataClient;
import com.example.tflmcpserver.model.StationToiletErrorCode;
import com.example.tflmcpserver.model.StationToiletLookupRequest;
import com.example.tflmcpserver.model.StationToiletToolResponse;
import io.github.resilience4j.ratelimiter.RateLimiter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StationToiletServiceTest {

	@Mock
	private TflStationDataClient tflStationDataClient;

	@Mock
	private RateLimiter stationToiletRateLimiter;

	@InjectMocks
	private StationToiletService stationToiletService;

	@Test
	void returnsRateLimitErrorWhenNoPermission() {
		StationToiletLookupRequest request = new StationToiletLookupRequest("Abbey Wood");
		when(stationToiletRateLimiter.acquirePermission()).thenReturn(false);

		StationToiletToolResponse response = stationToiletService.stationToilets(request);

		assertEquals(false, response.success());
		assertEquals(StationToiletErrorCode.RATE_LIMIT_EXCEEDED.name(), response.code());
		verifyNoInteractions(tflStationDataClient);
	}

	@Test
	void returnsDisambiguationWhenMultipleStationsMatch() {
		StationToiletLookupRequest request = new StationToiletLookupRequest("Shepherd's Bush");
		when(stationToiletRateLimiter.acquirePermission()).thenReturn(true);
		when(tflStationDataClient.findStationsByName("Shepherd's Bush", 5)).thenReturn(List.of(
				new TflStationDataClient.StationRecord("A", "Shepherd's Bush", "shepherd's bush"),
				new TflStationDataClient.StationRecord("B", "Shepherd's Bush Market", "shepherd's bush market")));

		StationToiletToolResponse response = stationToiletService.stationToilets(request);

		assertEquals(false, response.success());
		assertEquals(StationToiletErrorCode.DISAMBIGUATION_REQUIRED.name(), response.code());
		assertEquals(2, response.stationDisambiguation().size());
	}

	@Test
	void returnsToiletDetailsForSingleStationMatch() {
		StationToiletLookupRequest request = new StationToiletLookupRequest("Burnham");
		when(stationToiletRateLimiter.acquirePermission()).thenReturn(true);
		when(tflStationDataClient.findStationsByName("Burnham", 5))
				.thenReturn(List.of(new TflStationDataClient.StationRecord("910GBNHAM", "Burnham", "burnham")));
		when(tflStationDataClient.findToiletsByStationUniqueId("910GBNHAM", 25)).thenReturn(List
				.of(new TflStationDataClient.ToiletRecord("Located on platform 2", true, true, true, false, "Unisex")));

		StationToiletToolResponse response = stationToiletService.stationToilets(request);

		assertEquals(true, response.success());
		assertEquals("OK", response.code());
		assertEquals("Burnham", response.stationName());
		assertEquals(1, response.toilets().size());
		assertEquals(List.of(2), response.toilets().get(0).platformNumbers());
	}

	@Test
	void returnsNotFoundWhenNoStationMatches() {
		StationToiletLookupRequest request = new StationToiletLookupRequest("Unknown");
		when(stationToiletRateLimiter.acquirePermission()).thenReturn(true);
		when(tflStationDataClient.findStationsByName("Unknown", 5)).thenReturn(List.of());

		StationToiletToolResponse response = stationToiletService.stationToilets(request);

		assertEquals(false, response.success());
		assertEquals(StationToiletErrorCode.STATION_NOT_FOUND.name(), response.code());
	}
}
