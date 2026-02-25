package com.example.tflmcpserver.model;

public record JourneyOptionSummary(int durationMinutes, String startDateTime, String arrivalDateTime, int legCount) {
}
