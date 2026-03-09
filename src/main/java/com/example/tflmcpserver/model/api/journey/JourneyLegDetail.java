package com.example.tflmcpserver.model.api.journey;

import jakarta.annotation.Nullable;
import java.util.List;

public record JourneyLegDetail(int durationMinutes, @Nullable String departureTime, @Nullable String arrivalTime,
		@Nullable Double distanceMeters, @Nullable String instructionSummary, @Nullable String instructionDetailed,
		@Nullable String modeId, @Nullable String modeName, @Nullable String routeName, @Nullable String direction,
		@Nullable String interChangeDuration, @Nullable String interChangePosition, boolean disrupted,
		List<String> disruptionSummaries, List<String> plannedWorkDescriptions, List<String> stopPoints) {
}
