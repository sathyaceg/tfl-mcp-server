package com.example.tflmcpserver.model.api.station;

import jakarta.validation.constraints.NotBlank;

public record StationToiletLookupRequest(@NotBlank String stationName) {
}
