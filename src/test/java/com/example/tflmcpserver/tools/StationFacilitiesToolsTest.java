package com.example.tflmcpserver.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.tflmcpserver.model.StationToiletLookupRequest;
import com.example.tflmcpserver.model.StationToiletToolResponse;
import com.example.tflmcpserver.service.StationToiletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StationFacilitiesToolsTest {

	@Mock
	private StationToiletService stationToiletService;

	@InjectMocks
	private StationFacilitiesTools stationFacilitiesTools;

	@Test
	void delegatesToService() {
		StationToiletLookupRequest request = new StationToiletLookupRequest("Abbey Wood");
		StationToiletToolResponse expected = StationToiletToolResponse.error("RATE_LIMIT_EXCEEDED",
				"Rate limit exceeded for stationToilets.");
		when(stationToiletService.stationToilets(request)).thenReturn(expected);

		StationToiletToolResponse response = stationFacilitiesTools.stationToilets(request);

		assertEquals(expected, response);
		verify(stationToiletService).stationToilets(request);
	}
}
