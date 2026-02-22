package com.example.tflmcpserver.tools;

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
    public String planJourney(
            @ToolParam(description = "Start location or stop code") String from,
            @ToolParam(description = "Destination location or stop code") String to) {
        return journeyPlannerService.planJourney(from, to);
    }
}
