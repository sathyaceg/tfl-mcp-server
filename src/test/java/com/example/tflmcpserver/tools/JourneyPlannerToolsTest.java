package com.example.tflmcpserver.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.tflmcpserver.model.JourneyPlanRequest;
import com.example.tflmcpserver.service.JourneyPlannerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JourneyPlannerToolsTest {

    @Mock
    private JourneyPlannerService journeyPlannerService;

    @InjectMocks
    private JourneyPlannerTools journeyPlannerTools;

    @Test
    void delegatesToService() {
        JourneyPlanRequest request = new JourneyPlanRequest("From", "To");
        when(journeyPlannerService.planJourney(request)).thenReturn("tool-result");

        String response = journeyPlannerTools.planJourney(request);

        assertEquals("tool-result", response);
        verify(journeyPlannerService).planJourney(request);
    }
}
