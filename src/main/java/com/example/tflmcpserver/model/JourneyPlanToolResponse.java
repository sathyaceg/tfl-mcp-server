package com.example.tflmcpserver.model;

import jakarta.annotation.Nullable;
import java.util.List;

public record JourneyPlanToolResponse(boolean success, String code, String message,
		@Nullable List<JourneyOptionSummary> topJourneys) {

	public static JourneyPlanToolResponse success(List<JourneyOptionSummary> topJourneys) {
		return new JourneyPlanToolResponse(true, "OK", "Journey plan retrieved.", topJourneys);
	}

	public static JourneyPlanToolResponse error(String code, String message) {
		return new JourneyPlanToolResponse(false, code, message, null);
	}
}
