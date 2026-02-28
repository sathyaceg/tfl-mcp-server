package com.example.tflmcpserver.tools;

import com.example.tflmcpserver.model.StationToiletLookupRequest;
import com.example.tflmcpserver.model.StationToiletToolResponse;
import com.example.tflmcpserver.service.StationToiletService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class StationFacilitiesTools {

	private final StationToiletService stationToiletService;

	public StationFacilitiesTools(StationToiletService stationToiletService) {
		this.stationToiletService = stationToiletService;
	}

	@Tool(description = "Lookup toilet facilities for a station, including platform/location details when available.")
	public StationToiletToolResponse stationToilets(
			@ToolParam(description = "Station toilet lookup request containing stationName") StationToiletLookupRequest request) {
		return stationToiletService.stationToilets(request);
	}
}
