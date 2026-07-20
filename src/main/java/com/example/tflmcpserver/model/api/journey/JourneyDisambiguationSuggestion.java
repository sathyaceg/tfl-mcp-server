package com.example.tflmcpserver.model.api.journey;

import jakarta.annotation.Nullable;
import java.util.List;

public record JourneyDisambiguationSuggestion(String parameterValue, Integer matchQuality, @Nullable String commonName,
		@Nullable String placeType, @Nullable String naptanId, List<String> modes, @Nullable Double latitude,
		@Nullable Double longitude) {

	public JourneyDisambiguationSuggestion(String parameterValue, Integer matchQuality) {
		this(parameterValue, matchQuality, null, null, null, List.of(), null, null);
	}
}
