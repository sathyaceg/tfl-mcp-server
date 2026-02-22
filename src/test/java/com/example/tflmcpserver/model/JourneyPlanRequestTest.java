package com.example.tflmcpserver.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class JourneyPlanRequestTest {

    @Test
    void exposesFromAndToValues() {
        JourneyPlanRequest request = new JourneyPlanRequest("Waterloo", "Victoria");

        assertEquals("Waterloo", request.from());
        assertEquals("Victoria", request.to());
    }
}
