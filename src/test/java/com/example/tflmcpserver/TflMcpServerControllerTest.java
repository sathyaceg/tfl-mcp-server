package com.example.tflmcpserver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TflMcpServerControllerTest {

    @Test
    void helloReturnsExpectedMessage() {
        TflMcpServerController controller = new TflMcpServerController();

        assertEquals("tfl-mcp-server is running", controller.hello());
    }
}
