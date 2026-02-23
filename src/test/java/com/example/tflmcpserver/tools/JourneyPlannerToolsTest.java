package com.example.tflmcpserver.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.tflmcpserver.model.JourneyPlanRequest;
import com.example.tflmcpserver.model.JourneyPlanToolResponse;
import com.example.tflmcpserver.service.JourneyPlannerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JourneyPlannerToolsTest {

	@Mock
	private JourneyPlannerService journeyPlannerService;

	@InjectMocks
	private JourneyPlannerTools journeyPlannerTools;

	@Test
	void delegatesToService() {
		JourneyPlanRequest request = new JourneyPlanRequest("From", "To", null);
		JourneyPlanToolResponse expected = JourneyPlanToolResponse.success("payload");
		when(journeyPlannerService.planJourney(request)).thenReturn(expected);

		JourneyPlanToolResponse response = journeyPlannerTools.planJourney(request);

		assertEquals(expected, response);
		verify(journeyPlannerService).planJourney(request);
	}
}
