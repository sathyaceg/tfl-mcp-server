package com.example.tflmcpserver.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.tflmcpserver.service.JourneyPlannerService;
import com.example.tflmcpserver.service.StationToiletService;
import com.example.tflmcpserver.tools.JourneyPlannerTools;
import com.example.tflmcpserver.tools.StationFacilitiesTools;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.tool.ToolCallbackProvider;

class McpToolConfigTest {

	@Test
	void createsToolCallbackProvider() {
		JourneyPlannerService service = Mockito.mock(JourneyPlannerService.class);
		StationToiletService stationToiletService = Mockito.mock(StationToiletService.class);
		JourneyPlannerTools tools = new JourneyPlannerTools(service);
		StationFacilitiesTools stationFacilitiesTools = new StationFacilitiesTools(stationToiletService);
		McpToolConfig config = new McpToolConfig();

		ToolCallbackProvider provider = config.journeyToolCallbacks(tools, stationFacilitiesTools);

		assertNotNull(provider);
		assertFalse(provider.getToolCallbacks().length == 0);
	}
}
