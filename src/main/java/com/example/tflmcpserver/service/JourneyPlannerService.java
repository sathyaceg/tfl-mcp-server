package com.example.tflmcpserver.service;

import com.example.tflmcpserver.client.TflJourneyClient;
import org.springframework.stereotype.Service;

@Service
public class JourneyPlannerService {

    private final TflJourneyClient tflJourneyClient;

    public JourneyPlannerService(TflJourneyClient tflJourneyClient) {
        this.tflJourneyClient = tflJourneyClient;
    }

    public String planJourney(String from, String to) {
        return tflJourneyClient.journeyResults(from, to);
    }
}
