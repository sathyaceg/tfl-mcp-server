package com.example.tflmcpserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.tflmcpserver.client.TflJourneyClient;
import com.example.tflmcpserver.model.JourneyPlanRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JourneyPlannerServiceTest {

    @Mock
    private TflJourneyClient tflJourneyClient;

    @InjectMocks
    private JourneyPlannerService journeyPlannerService;

    @Test
    void delegatesToClient() {
        JourneyPlanRequest request = new JourneyPlanRequest("A", "B");
        when(tflJourneyClient.journeyResults(request)).thenReturn("result");

        String response = journeyPlannerService.planJourney(request);

        assertEquals("result", response);
        verify(tflJourneyClient).journeyResults(request);
    }
}
