package com.example.tflmcpserver.model;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record JourneyPlanRequest(@NotBlank String from, @NotBlank String to, @Nullable Boolean needAccessibleRoute) {
}
