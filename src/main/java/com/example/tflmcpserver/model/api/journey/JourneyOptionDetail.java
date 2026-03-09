package com.example.tflmcpserver.model.api.journey;

import jakarta.annotation.Nullable;
import java.util.List;

public record JourneyOptionDetail(int durationMinutes, String startDateTime, String arrivalDateTime, int legCount,
		@Nullable String description, boolean alternativeRoute, @Nullable Integer totalFarePence,
		List<JourneyLegDetail> legs) {
}
