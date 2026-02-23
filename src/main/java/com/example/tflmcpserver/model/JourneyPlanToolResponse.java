package com.example.tflmcpserver.model;

import jakarta.annotation.Nullable;

public record JourneyPlanToolResponse(boolean success, String code, String message, @Nullable String journeyJson) {

	public static JourneyPlanToolResponse success(String journeyJson) {
		return new JourneyPlanToolResponse(true, "OK", "Journey plan retrieved.", journeyJson);
	}

	public static JourneyPlanToolResponse error(String code, String message) {
		return new JourneyPlanToolResponse(false, code, message, null);
	}
}
