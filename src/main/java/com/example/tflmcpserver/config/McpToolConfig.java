package com.example.tflmcpserver.config;

import com.example.tflmcpserver.tools.JourneyPlannerTools;
import com.example.tflmcpserver.tools.StationFacilitiesTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpToolConfig {

	@Bean
	public ToolCallbackProvider journeyToolCallbacks(JourneyPlannerTools journeyPlannerTools,
			StationFacilitiesTools stationFacilitiesTools) {
		return MethodToolCallbackProvider.builder().toolObjects(journeyPlannerTools, stationFacilitiesTools).build();
	}
}
