package com.example.tflmcpserver.model;

import jakarta.validation.constraints.NotBlank;

public record StationToiletLookupRequest(@NotBlank String stationName) {
}
