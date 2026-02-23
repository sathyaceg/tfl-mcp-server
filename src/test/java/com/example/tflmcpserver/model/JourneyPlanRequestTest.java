package com.example.tflmcpserver.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class JourneyPlanRequestTest {

	@Test
	void exposesJourneyRequestValues() {
		JourneyPlanRequest request = new JourneyPlanRequest("Waterloo", "Victoria", true);

		assertEquals("Waterloo", request.from());
		assertEquals("Victoria", request.to());
		assertEquals(true, request.needAccessibleRoute());
	}
}
