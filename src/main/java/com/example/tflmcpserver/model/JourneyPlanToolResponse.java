package com.example.tflmcpserver.model;

import jakarta.annotation.Nullable;
import java.util.List;

public record JourneyPlanToolResponse(boolean success, String code, String message,
		@Nullable List<JourneyOptionSummary> topJourneys,
		@Nullable List<JourneyDisambiguationSuggestion> fromLocationDisambiguation,
		@Nullable List<JourneyDisambiguationSuggestion> toLocationDisambiguation) {

	public static JourneyPlanToolResponse success(List<JourneyOptionSummary> topJourneys) {
		return new JourneyPlanToolResponse(true, "OK", "Journey plan retrieved.", topJourneys, null, null);
	}

	public static JourneyPlanToolResponse disambiguationRequired(
			List<JourneyDisambiguationSuggestion> fromLocationDisambiguation,
			List<JourneyDisambiguationSuggestion> toLocationDisambiguation) {
		return new JourneyPlanToolResponse(false, JourneyPlannerErrorCode.DISAMBIGUATION_REQUIRED.name(),
				"Input location is ambiguous. Choose one of the suggested parameterValue values.", null,
				fromLocationDisambiguation, toLocationDisambiguation);
	}

	public static JourneyPlanToolResponse error(String code, String message) {
		return new JourneyPlanToolResponse(false, code, message, null, null, null);
	}
}
