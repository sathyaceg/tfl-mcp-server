package com.example.tflmcpserver.model;

import jakarta.annotation.Nullable;
import java.util.List;

public record StationToiletToolResponse(boolean success, String code, String message, @Nullable String stationName,
		@Nullable String stationUniqueId, @Nullable List<StationToiletDetail> toilets,
		@Nullable List<StationDisambiguationSuggestion> stationDisambiguation) {

	public static StationToiletToolResponse success(String stationName, String stationUniqueId,
			List<StationToiletDetail> toilets) {
		String message = toilets.isEmpty()
				? "No toilet facilities found for station."
				: "Station toilet facilities retrieved.";
		return new StationToiletToolResponse(true, "OK", message, stationName, stationUniqueId, toilets, null);
	}

	public static StationToiletToolResponse disambiguationRequired(
			List<StationDisambiguationSuggestion> stationDisambiguation) {
		return new StationToiletToolResponse(false, StationToiletErrorCode.DISAMBIGUATION_REQUIRED.name(),
				"Input station name is ambiguous. Choose one of the suggested stationUniqueId values.", null, null,
				null, stationDisambiguation);
	}

	public static StationToiletToolResponse error(String code, String message) {
		return new StationToiletToolResponse(false, code, message, null, null, null, null);
	}
}
