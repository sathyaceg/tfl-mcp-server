package com.example.tflmcpserver.model;

import java.util.List;

public record StationToiletDetail(String location, List<Integer> platformNumbers, boolean accessible,
		boolean babyChanging, boolean insideGateLine, boolean feeCharged, String type) {
}
