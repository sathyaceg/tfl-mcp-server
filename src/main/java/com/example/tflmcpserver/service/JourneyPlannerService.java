package com.example.tflmcpserver.service;

import com.example.tflmcpserver.client.TflJourneyClient;
import com.example.tflmcpserver.model.JourneyPlanRequest;
import org.springframework.stereotype.Service;

@Service
public class JourneyPlannerService {

    private final TflJourneyClient tflJourneyClient;

    public JourneyPlannerService(TflJourneyClient tflJourneyClient) {
        this.tflJourneyClient = tflJourneyClient;
    }

    public String planJourney(JourneyPlanRequest request) {
        return tflJourneyClient.journeyResults(request);
    }
}
