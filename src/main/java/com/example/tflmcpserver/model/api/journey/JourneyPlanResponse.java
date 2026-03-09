package com.example.tflmcpserver.model.api.journey;

import com.example.tflmcpserver.model.JourneyPlannerErrorCode;
import jakarta.annotation.Nullable;
import java.util.List;

public record JourneyPlanResponse(boolean success, String code, String message,
		@Nullable List<JourneyOptionDetail> topJourneyDetails,
		@Nullable List<JourneyDisambiguationSuggestion> fromLocationDisambiguation,
		@Nullable List<JourneyDisambiguationSuggestion> toLocationDisambiguation) {

	public static JourneyPlanResponse success(List<JourneyOptionDetail> topJourneyDetails) {
		return new JourneyPlanResponse(true, "OK", "Journey plan retrieved.", topJourneyDetails, null, null);
	}

	public static JourneyPlanResponse disambiguationRequired(
			List<JourneyDisambiguationSuggestion> fromLocationDisambiguation,
			List<JourneyDisambiguationSuggestion> toLocationDisambiguation) {
		return new JourneyPlanResponse(false, JourneyPlannerErrorCode.DISAMBIGUATION_REQUIRED.name(),
				"Input location is ambiguous. Choose one of the suggested parameterValue values.", null,
				fromLocationDisambiguation, toLocationDisambiguation);
	}

	public static JourneyPlanResponse error(String code, String message) {
		return new JourneyPlanResponse(false, code, message, null, null, null);
	}
}
