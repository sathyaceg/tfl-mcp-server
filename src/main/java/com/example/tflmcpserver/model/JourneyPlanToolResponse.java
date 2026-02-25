package com.example.tflmcpserver.model;

import jakarta.annotation.Nullable;
import java.util.List;

public record JourneyPlanToolResponse(boolean success, String code, String message,
		@Nullable List<JourneyOptionSummary> topJourneys,
		@Nullable List<JourneyDisambiguationSuggestion> disambiguationOptions) {

	public static JourneyPlanToolResponse success(List<JourneyOptionSummary> topJourneys) {
		return new JourneyPlanToolResponse(true, "OK", "Journey plan retrieved.", topJourneys, null);
	}

	public static JourneyPlanToolResponse disambiguationRequired(
			List<JourneyDisambiguationSuggestion> disambiguationOptions) {
		return new JourneyPlanToolResponse(false, JourneyPlannerErrorCode.DISAMBIGUATION_REQUIRED.name(),
				"Input location is ambiguous. Choose one of the suggested parameterValue values.", null,
				disambiguationOptions);
	}

	public static JourneyPlanToolResponse error(String code, String message) {
		return new JourneyPlanToolResponse(false, code, message, null, null);
	}
}
