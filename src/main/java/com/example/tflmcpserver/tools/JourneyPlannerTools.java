package com.example.tflmcpserver.tools;

import com.example.tflmcpserver.model.JourneyPlanRequest;
import com.example.tflmcpserver.model.JourneyPlanToolResponse;
import com.example.tflmcpserver.service.JourneyPlannerService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class JourneyPlannerTools {

	private final JourneyPlannerService journeyPlannerService;

	public JourneyPlannerTools(JourneyPlannerService journeyPlannerService) {
		this.journeyPlannerService = journeyPlannerService;
	}

	@Tool(description = "Plan a journey in London using TfL Journey Planner.")
	public JourneyPlanToolResponse planJourney(
			@ToolParam(description = "Journey request with from and to locations") JourneyPlanRequest request) {
		return journeyPlannerService.planJourney(request);
	}
}
